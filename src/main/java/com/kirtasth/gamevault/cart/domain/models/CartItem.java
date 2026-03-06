package com.kirtasth.gamevault.cart.domain.models;

import lombok.Builder;

import java.time.Instant;

@Builder
public record CartItem(
        Long id,
        Long cartId,
        Long gameId,
        Integer quantity,
        Double priceAtAddition,
        Instant createdAt,
        Instant updatedAt
) {
}
