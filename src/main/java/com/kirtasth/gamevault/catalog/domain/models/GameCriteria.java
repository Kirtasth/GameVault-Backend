package com.kirtasth.gamevault.catalog.domain.models;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record GameCriteria(
        String title,
        Double minPrice,
        Double maxPrice,
        String developerName,
        List<String> gameTags,
        Instant fromReleaseTime,
        Instant toReleaseTime
) {
}
