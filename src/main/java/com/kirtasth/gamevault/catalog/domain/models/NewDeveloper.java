package com.kirtasth.gamevault.catalog.domain.models;

import lombok.Builder;

@Builder
public record NewDeveloper(
        String name,
        String description
) {
}
