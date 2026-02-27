package com.kirtasth.gamevault.catalog.infrastructure.dtos.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record NewGameRequest(

        @NotBlank
        String title,

        String description,

        @NotNull
        @Min(0)
        Double price,

        String releaseDate,

        MultipartFile image
) {
}
