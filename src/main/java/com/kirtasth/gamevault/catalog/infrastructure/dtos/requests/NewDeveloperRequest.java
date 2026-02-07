package com.kirtasth.gamevault.catalog.infrastructure.dtos.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewDeveloperRequest(

        @NotNull
        @Min(1)
        Long userId,

        @NotBlank
        String name,

        @NotBlank
        String description

) {
}
