package com.kirtasth.gamevault.cart.domain.models;

import com.kirtasth.gamevault.common.domain.models.enums.CartStatusEnum;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ShoppingCart(
        Long id,
        Long userId,
        List<CartItem> items,
        CartStatusEnum status,
        Double totalPrice,
        Instant createdAt,
        Instant updatedAt
) {
}
