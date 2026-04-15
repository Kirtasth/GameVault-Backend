package com.kirtasth.gamevault.checkout.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirtasth.gamevault.cart.application.exceptions.EmptyCartException;
import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.cart.domain.ports.in.CartServicePort;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.checkout.application.exceptions.KeysNotAvailableException;
import com.kirtasth.gamevault.checkout.application.exceptions.StripeIntegrationException;
import com.kirtasth.gamevault.checkout.domain.models.GameKey;
import com.kirtasth.gamevault.checkout.domain.ports.in.CheckoutUseCase;
import com.kirtasth.gamevault.checkout.domain.ports.out.CatalogPort;
import com.kirtasth.gamevault.checkout.domain.ports.out.GameKeyRepository;
import com.kirtasth.gamevault.checkout.domain.ports.out.StripeSessionPort;
import com.kirtasth.gamevault.checkout.infrastructure.adapters.StripeAdapterFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CheckoutServiceAdapter implements CheckoutUseCase {

    private final CartServicePort cartServicePort;
    private final CatalogPort catalogPort;
    private final StripeSessionPort stripeSessionPort;
    private final StripeAdapterFactory stripeAdapterFactory;
    private final GameKeyRepository gameKeyRepository;
    private final ObjectMapper objectMapper;
    private final long expirationMinutes;

    public CheckoutServiceAdapter(
            CartServicePort cartServicePort,
            CatalogPort catalogPort,
            StripeSessionPort stripeSessionPort,
            StripeAdapterFactory stripeAdapterFactory,
            GameKeyRepository gameKeyRepository,
            ObjectMapper objectMapper,
            @Value("${stripe.checkout.expiration-minutes}") long expirationMinutes) {
        this.cartServicePort = cartServicePort;
        this.catalogPort = catalogPort;
        this.stripeSessionPort = stripeSessionPort;
        this.stripeAdapterFactory = stripeAdapterFactory;
        this.gameKeyRepository = gameKeyRepository;
        this.objectMapper = objectMapper;
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    @Transactional
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

        // Check availability and reserve keys
        Instant deadline = Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES);
        List<GameKey> keysToReserve = new ArrayList<>();

        for (CartItem item : cart.items()) {
            List<GameKey> availableKeys = gameKeyRepository.findAvailableKeysByGameId(item.gameId(), item.quantity());
            if (availableKeys.size() < item.quantity()) {
                Game game = games.get(item.gameId());
                throw new KeysNotAvailableException("Not enough keys available for: " + (game != null ? game.title() : "Game ID " + item.gameId()));
            }
            availableKeys.forEach(key -> {
                key.setReservedByUserId(userId);
                key.setReservedDeadline(deadline);
            });
            keysToReserve.addAll(availableKeys);
        }

        gameKeyRepository.saveAll(keysToReserve);

        return stripeSessionPort.createCheckoutSession(cart, games);
    }

    @Override
    public void handleStripeEvent(String payload, String sigHeader) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            String eventType = rootNode.get("type").asText();

            log.info("Processing Stripe event: {}", eventType);

            stripeAdapterFactory.getAdapter(eventType)
                    .ifPresentOrElse(
                            adapter -> adapter.handleEvent(payload, sigHeader),
                            () -> log.warn("No adapter found for event type: {}", eventType)
                    );
        } catch (Exception e) {
            log.error("Error processing Stripe event: {}", e.getMessage(), e);
            throw new StripeIntegrationException("Error processing Stripe event");
        }
    }
}
