package com.kirtasth.gamevault.catalog.infrastructure.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record NewDeveloperRequest(

        @NotBlank
        String name,

        @NotBlank
        String description

) {
}
