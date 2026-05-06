package com.kirtasth.gamevault.checkout.infrastructure.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PurchasedGameKeyResponse {
    private Long gameId;
    private String gameTitle;
    private String imageUrl;
    private String keyValue;
    private Instant purchasedAt;
}
