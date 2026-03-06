package com.kirtasth.gamevault.cart.application.exceptions;

import com.kirtasth.gamevault.common.application.exception.ResourceConflictException;

public class ShoppingCartUpdateException extends ResourceConflictException {
    public ShoppingCartUpdateException(Long userId) {
        super("Failed to update shopping cart for user with ID " + userId + ".");
    }
}
