package com.kirtasth.gamevault.checkout.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameKey {
    private Long id;
    private Long gameId;
    private String keyValue;
    private Boolean isUsed;
    private Long orderItemId;
    private Instant usedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
