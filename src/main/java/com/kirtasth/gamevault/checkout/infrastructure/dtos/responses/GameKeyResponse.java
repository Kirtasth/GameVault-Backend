package com.kirtasth.gamevault.checkout.infrastructure.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class GameKeyResponse {
    private Long id;
    private String keyValue;
    private Boolean isUsed;
    private Instant createdAt;
}
