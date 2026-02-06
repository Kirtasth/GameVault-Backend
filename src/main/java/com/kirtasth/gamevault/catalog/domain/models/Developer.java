package com.kirtasth.gamevault.catalog.domain.models;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record Developer(
        Long id,
        String name,
        String description,
        List<Game> games,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
}
