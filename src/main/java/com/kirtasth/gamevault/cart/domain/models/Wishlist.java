package com.kirtasth.gamevault.cart.domain.models;

import lombok.Builder;

import java.time.Instant;

@Builder
public record Wishlist(
        Long userId,
        Long gameId,
        Instant createdAt
) {
}
