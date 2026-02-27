package com.kirtasth.gamevault.catalog.domain.models;

import com.kirtasth.gamevault.common.models.enums.GameStatusEnum;
import lombok.Builder;

import java.time.Instant;

@Builder
public record GameStatus(
        Long gameId,
        GameStatusEnum status,
        String description,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {

}
