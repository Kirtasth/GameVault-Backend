package com.kirtasth.gamevault.catalog.domain.models;

import java.time.Instant;

public record GameTag(
        Long id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {

}
