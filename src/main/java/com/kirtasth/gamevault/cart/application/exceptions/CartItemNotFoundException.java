package com.kirtasth.gamevault.cart.application.exceptions;

import com.kirtasth.gamevault.common.application.exception.ResourceNotFoundException;

public class CartItemNotFoundException extends ResourceNotFoundException {
    public CartItemNotFoundException(Long itemId) {
        super("Cart item with ID " + itemId + " not found.");
    }
}
