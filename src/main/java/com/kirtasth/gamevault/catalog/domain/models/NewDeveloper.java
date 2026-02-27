package com.kirtasth.gamevault.catalog.domain.models;

import lombok.Builder;

@Builder
public record NewDeveloper(
        Long userId,
        String name,
        String description
) {
}
