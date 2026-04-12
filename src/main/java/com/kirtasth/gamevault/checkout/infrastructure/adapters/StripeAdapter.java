package com.kirtasth.gamevault.checkout.infrastructure.adapters;

import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.checkout.domain.ports.out.StripePort;
import com.stripe.StripeClient;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
public class StripeAdapter implements StripePort {

    private final StripeClient client;
    private final String successUrl;
    private final String cancelUrl;
    private final String webhookSecret;

    public StripeAdapter(
            StripeClient client,
            @Value("${stripe.webhook.secret}") String webhookSecret,
            @Value("${stripe.success.url}") String successUrl,
            @Value("${stripe.cancel.url}") String cancelUrl) {
        this.client = client;
        this.webhookSecret = webhookSecret;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        
        if (this.webhookSecret == null || this.webhookSecret.isBlank()) {
            log.warn("Stripe webhook secret is NULL or BLANK! Signature verification will fail.");
        }
    }

    @Override
    public String createCheckoutSession(ShoppingCart cart, Map<Long, Game> games) {
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .putMetadata("cart_id", cart.id().toString())
                .putMetadata("user_id", cart.userId().toString());

        var productDataBuilder = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName("GameVault Order");

        StringBuilder description = new StringBuilder();
        for (CartItem item : cart.items()) {
            Game game = games.get(item.gameId());
            if (game == null) {
                continue;
            }

            if (!description.isEmpty()) {
                description.append(", ");
            }
            description.append(game.title()).append(" (x").append(item.quantity()).append(")");

            if (game.imageUrl() != null) {
                String fullUrl = game.imageUrl();

                if ((fullUrl.startsWith("http://") || fullUrl.startsWith("https://")) && !fullUrl.contains("localhost")) {
                    productDataBuilder.addImage(fullUrl);
                }
            }
        }

        productDataBuilder.setDescription(description.toString());

        paramsBuilder.addLineItem(
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("eur")
                                        .setUnitAmountDecimal(BigDecimal.valueOf(cart.totalPrice()).multiply(BigDecimal.valueOf(100)))
                                        .setProductData(productDataBuilder.build())
                                        .build()
                        )
                        .build()
        );

        try {
            Session session = client.v1().checkout().sessions().create(paramsBuilder.build());
            return session.getUrl();
        } catch (StripeException e) {
            throw new RuntimeException("Error creating Stripe checkout session: " + e.getMessage());
        }
    }

    @Override
    public Event verifyWebhookSignature(String payload, String sigHeader) {
        String secretHint = (webhookSecret != null && webhookSecret.length() > 7) 
            ? webhookSecret.substring(0, 7) + "..." 
            : "MISSING/SHORT";
            
        log.info("Verifying Stripe signature. Payload length: {}. sigHeader hint: {}. Secret hint: {}", 
                payload != null ? payload.length() : 0,
                sigHeader != null && sigHeader.length() > 20 ? sigHeader.substring(0, 20) + "..." : "N/A",
                secretHint);
                
        try {
            return Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Stripe signature verification failed. This usually means the STRIPE_WEBHOOK_SECRET is wrong or the payload body was modified. Error: {}", e.getMessage());
            throw new RuntimeException("Stripe webhook signature verification failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during Stripe signature verification: {}", e.getMessage(), e);
            throw e;
        }
    }
}
