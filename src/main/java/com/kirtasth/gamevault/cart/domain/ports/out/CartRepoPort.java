package com.kirtasth.gamevault.cart.domain.ports.out;

import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;

import java.util.Optional;

public interface CartRepoPort {
    ShoppingCart save(ShoppingCart cart);

    CartItem findById(Long itemId);

    Optional<ShoppingCart> findOpenedByUserId(Long userId);

    CartItem save(CartItem item);

}
