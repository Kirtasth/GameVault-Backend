package com.kirtasth.gamevault.catalog.domain.models;

import java.time.Instant;

public record NewGame(
        Long developerId,
        String title,
        String description,
        Double price,
        Instant releaseDate
) {
}
