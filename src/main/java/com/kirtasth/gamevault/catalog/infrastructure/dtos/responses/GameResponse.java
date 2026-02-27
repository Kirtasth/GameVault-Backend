package com.kirtasth.gamevault.catalog.infrastructure.dtos.responses;

import com.kirtasth.gamevault.catalog.domain.models.GameStatus;
import com.kirtasth.gamevault.catalog.domain.models.GameTag;

import java.time.Instant;
import java.util.List;

public record GameResponse(
        Long id,
        String title,
        String description,
        Double price,
        Instant releaseDate,
        Instant createdAt,
        Instant updatedAt,
        String imageUrl,
        List<GameStatus> gameStatuses,
        List<GameTag> tags
) {
}
