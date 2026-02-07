package com.kirtasth.gamevault.catalog.infrastructure.dtos.requests;

public record NewDeveloperRequest(
        Long userId,
        String name,
        String description
) {
}
