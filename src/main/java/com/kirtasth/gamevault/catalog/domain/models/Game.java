package com.kirtasth.gamevault.catalog.domain.models;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record Game(
        Long id,
        Long developerId,
        String title,
        String description,
        Double price,
        List<GameStatus> gameStatuses,
        List<GameTag> tags,
        Instant releaseDate,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {

}
