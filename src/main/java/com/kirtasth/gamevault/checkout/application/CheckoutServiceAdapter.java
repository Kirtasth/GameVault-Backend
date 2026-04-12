package com.kirtasth.gamevault.checkout.application;

import com.kirtasth.gamevault.cart.application.exceptions.EmptyCartException;
import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.cart.domain.ports.in.CartServicePort;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.checkout.domain.models.Order;
import com.kirtasth.gamevault.checkout.domain.models.OrderItem;
import com.kirtasth.gamevault.checkout.domain.models.OrderStatus;
import com.kirtasth.gamevault.checkout.domain.ports.in.CheckoutUseCase;
import com.kirtasth.gamevault.checkout.domain.ports.out.CatalogPort;
import com.kirtasth.gamevault.checkout.domain.ports.out.GameKeyRepository;
import com.kirtasth.gamevault.checkout.domain.ports.out.OrderRepository;
import com.kirtasth.gamevault.checkout.domain.ports.out.StripePort;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckoutServiceAdapter implements CheckoutUseCase {

    private final CartServicePort cartServicePort;
    private final CatalogPort catalogPort;
    private final StripePort stripePort;
    private final OrderRepository orderRepository;
    private final GameKeyRepository gameKeyRepository;

    @Override
    public String createCheckoutSession(Long userId) {
        ShoppingCart cart = cartServicePort.getCart(userId);
        
        if (cart.items().isEmpty()) {
            throw new EmptyCartException("Cart is empty");
        }

        List<Long> gameIds = cart.items().stream()
                .map(CartItem::gameId)
                .toList();

        Map<Long, Game> games = catalogPort.findGamesByIds(gameIds).stream()
                .collect(Collectors.toMap(Game::id, Function.identity()));

        return stripePort.createCheckoutSession(cart, games);
    }

    @Override
    @Transactional
    public void handleStripeWebhook(String payload, String sigHeader) {
        Event event = stripePort.verifyWebhookSignature(payload, sigHeader);

        log.info("Received Stripe event: {}. Version: {}", event.getType(), event.getApiVersion());

        if ("checkout.session.completed".equals(event.getType())) {
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

        // Assign game keys
        for (OrderItem savedItem : savedOrder.getItems()) {
            gameKeyRepository.findRandomUnusedKeyByGameId(savedItem.getGameId())
                    .ifPresentOrElse(key -> {
                        key.setIsUsed(true);
                        key.setUsedAt(Instant.now());
                        key.setOrderItemId(savedItem.getId());
                        gameKeyRepository.save(key);
                    }, () -> log.error("NO GAME KEY AVAILABLE FOR GAME ID: {}", savedItem.getGameId()));
        }

        // Clear cart
        cartServicePort.clearCart(userId);
        log.info("Order {} created and keys assigned for user {}", savedOrder.getId(), userId);
    }
}
