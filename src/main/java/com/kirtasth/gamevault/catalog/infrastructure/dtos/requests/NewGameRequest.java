package com.kirtasth.gamevault.catalog.infrastructure.dtos.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewGameRequest(

        @NotNull
        @Min(1)
        Long developerId,

        @NotBlank
        String title,

        String description,

        @NotNull
        @Min(0)
        Double price,

        String releaseDate
) {
}
