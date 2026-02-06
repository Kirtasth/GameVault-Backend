package com.kirtasth.gamevault.catalog.domain.models;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record DeveloperCriteria(
        String name,
        List<Game> games,
        Instant fromReleaseTime,
        Instant toReleaseTime
) {
}
