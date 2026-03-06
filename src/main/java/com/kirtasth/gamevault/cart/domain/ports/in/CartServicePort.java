package com.kirtasth.gamevault.cart.domain.ports.in;

import com.kirtasth.gamevault.cart.domain.models.AddItemPetition;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;

public interface CartServicePort {
    ShoppingCart getCart(Long userId);
    ShoppingCart addItemToCart(Long userId, AddItemPetition addItemPetition);
    ShoppingCart removeItemFromCart(Long userId, Long itemId);
    void clearCart(Long userId);
}
