package com.kirtasth.gamevault.cart.infrastructure.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class WishlistResponse {
    private Long userId;
    private Long gameId;
    private Instant createdAt;
}
