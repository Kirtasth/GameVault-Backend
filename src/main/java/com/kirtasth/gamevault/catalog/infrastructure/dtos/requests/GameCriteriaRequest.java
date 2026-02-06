package com.kirtasth.gamevault.catalog.infrastructure.dtos.requests;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record GameCriteriaRequest(
        String title,
        Double minPrice,
        Double maxPrice,
        String developerName,
        List<String> gameTags,
        Instant fromReleaseTime,
        Instant toReleaseTime
) {
}
