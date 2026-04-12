package com.kirtasth.gamevault.checkout.domain.ports.out;

import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.stripe.model.Event;

import java.util.Map;

public interface StripePort {
    String createCheckoutSession(ShoppingCart cart, Map<Long, Game> games);
    Event verifyWebhookSignature(String payload, String sigHeader);
}
