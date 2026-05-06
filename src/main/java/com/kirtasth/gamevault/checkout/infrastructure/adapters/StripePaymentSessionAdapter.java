package com.kirtasth.gamevault.checkout.infrastructure.adapters;

import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.checkout.application.exceptions.PaymentIntegrationException;
import com.kirtasth.gamevault.checkout.domain.ports.out.PaymentSessionPort;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Slf4j
@Component
public class StripePaymentSessionAdapter implements PaymentSessionPort {

    private final StripeClient client;
    private final String successUrl;
    private final String cancelUrl;
    private final long expirationMinutes;

    public StripePaymentSessionAdapter(
            StripeClient client,
            @Value("${stripe.success.url}") String successUrl,
            @Value("${stripe.cancel.url}") String cancelUrl,
            @Value("${stripe.checkout.expiration-minutes}") long expirationMinutes) {
        this.client = client;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public String createCheckoutSession(ShoppingCart cart, Map<Long, Game> games) {
        long expiresAt = Instant.now()
                .plus(expirationMinutes, ChronoUnit.MINUTES)
                .getEpochSecond();

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSubmitType(SessionCreateParams.SubmitType.PAY)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setExpiresAt(expiresAt)
                .putMetadata("user_id", cart.userId().toString());

        for (CartItem item : cart.items()) {
            Game game = games.get(item.gameId());
            if (game == null) {
                continue;
            }

            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) item.quantity())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("eur")
                                            .setUnitAmountDecimal(BigDecimal.valueOf(game.price()).multiply(BigDecimal.valueOf(100)))
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(game.title())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            );
        }

        try {
            Session session = client.v1().checkout().sessions().create(paramsBuilder.build());
            return session.getUrl();
        } catch (StripeException e) {
            throw new PaymentIntegrationException("Error creating Stripe checkout session: " + e.getMessage());
        }
    }
}
