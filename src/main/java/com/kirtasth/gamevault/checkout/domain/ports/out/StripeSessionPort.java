package com.kirtasth.gamevault.checkout.domain.ports.out;

import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.catalog.domain.models.Game;

import java.util.Map;

public interface StripeSessionPort {
    String createCheckoutSession(ShoppingCart cart, Map<Long, Game> games);
}
