package com.kirtasth.gamevault.checkout.infrastructure.adapters;

import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.cart.domain.ports.in.CartServicePort;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.checkout.application.exceptions.PaymentIntegrationException;
import com.kirtasth.gamevault.checkout.domain.models.GameKey;
import com.kirtasth.gamevault.checkout.domain.models.Order;
import com.kirtasth.gamevault.checkout.domain.models.OrderItem;
import com.kirtasth.gamevault.checkout.domain.models.OrderStatus;
import com.kirtasth.gamevault.checkout.domain.ports.out.CatalogPort;
import com.kirtasth.gamevault.checkout.domain.ports.out.GameKeyRepository;
import com.kirtasth.gamevault.checkout.domain.ports.out.OrderRepository;
import com.kirtasth.gamevault.checkout.domain.ports.out.PaymentEventHandler;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class StripeCheckoutSessionCompletedAdapter implements PaymentEventHandler {

    private final CartServicePort cartServicePort;
    private final CatalogPort catalogPort;
    private final OrderRepository orderRepository;
    private final GameKeyRepository gameKeyRepository;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Override
    public String getEventType() {
        return "checkout.session.completed";
    }

    @Override
    @Transactional
    public void handleEvent(String payload, Map<String, String> headers) {
        String sigHeader = getHeaderCaseInsensitive(headers, "Stripe-Signature");
        if (sigHeader == null) {
            log.error("Missing Stripe-Signature header");
            throw new PaymentIntegrationException("Missing Stripe-Signature header");
        }
        Event event = verifySignature(payload, sigHeader);

        log.info("Handling Stripe event: {}. Version: {}", event.getType(), event.getApiVersion());

        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        
        // Prefer safe deserialization (matches SDK version)
        StripeObject dataObject = deserializer.getObject().orElseGet(() -> {
            log.warn("Stripe API version mismatch detected (Event: {}, SDK: {}). Using unsafe deserialization.",
                    event.getApiVersion(), com.stripe.Stripe.API_VERSION);
            try {
                return deserializer.deserializeUnsafe();
            } catch (Exception e) {
                log.error("Failed to deserialize Stripe object: {}", e.getMessage());
                return null;
            }
        });

        if (dataObject instanceof Session session) {
            handleCheckoutSessionCompleted(session);
        } else {
            log.error("Expected Session object but received: {}", dataObject != null ? dataObject.getClass().getName() : "null");
        }
    }

    private Event verifySignature(String payload, String sigHeader) {
        try {
            return Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Stripe signature verification failed: {}", e.getMessage());
            throw new PaymentIntegrationException("Stripe webhook signature verification failed: " + e.getMessage());
        }
    }

    private String getHeaderCaseInsensitive(Map<String, String> headers, String key) {
        if (headers == null || key == null) return null;
        if (headers.containsKey(key)) return headers.get(key);
        
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (key.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void handleCheckoutSessionCompleted(Session session) {
        String userIdStr = session.getMetadata().get("user_id");
        if (userIdStr == null) {
            log.error("No user_id found in Stripe session metadata");
            return;
        }

        Long userId = Long.valueOf(userIdStr);
        ShoppingCart cart = cartServicePort.getCart(userId);
        
        if (cart.items().isEmpty()) {
            log.warn("Cart is empty for user {} upon checkout completion", userId);
            return;
        }

        List<Long> gameIds = cart.items().stream()
                .map(CartItem::gameId)
                .toList();

        Map<Long, Game> gamesMap = catalogPort.findGamesByIds(gameIds).stream()
                .collect(Collectors.toMap(Game::id, Function.identity()));

        Order order = Order.builder()
                .userId(userId)
                .totalPrice(cart.totalPrice())
                .status(OrderStatus.PAID)
                .stripeSessionId(session.getId())
                .items(new ArrayList<>())
                .build();

        for (CartItem cartItem : cart.items()) {
            Game game = gamesMap.get(cartItem.gameId());
            if (game == null) continue;

            for (int i = 0; i < cartItem.quantity(); i++) {
                OrderItem orderItem = OrderItem.builder()
                        .gameId(cartItem.gameId())
                        .purchasedPrice(game.price())
                        .build();
                order.getItems().add(orderItem);
            }
        }

        Order savedOrder = orderRepository.save(order);

        // Assign game keys from reservations
        List<GameKey> reservedKeys = gameKeyRepository.findReservedKeysByUserId(userId);
        
        for (OrderItem savedItem : savedOrder.getItems()) {
            reservedKeys.stream()
                    .filter(key -> key.getGameId().equals(savedItem.getGameId()) && key.getPurchasedByUserId() == null)
                    .findFirst()
                    .ifPresentOrElse(key -> {
                        key.setIsUsed(true);
                        key.setUsedAt(Instant.now());
                        key.setPurchasedByUserId(userId);
                        key.setReservedByUserId(null);
                        key.setReservedDeadline(null);
                        gameKeyRepository.save(key);
                        
                        savedItem.setGameKeyId(key.getId());
                    }, () -> log.error("NO RESERVED GAME KEY FOUND FOR GAME ID: {} AND USER: {}", savedItem.getGameId(), userId));
        }
        
        // Final save to update order items with game key IDs
        orderRepository.save(savedOrder);

        // Clear cart
        cartServicePort.clearCart(userId);
        log.info("Order {} created and keys assigned for user {}", savedOrder.getId(), userId);
    }
}
