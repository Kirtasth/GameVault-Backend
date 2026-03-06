package com.kirtasth.gamevault.cart.infrastructure.dtos.requests;

import jakarta.validation.constraints.Min;
import lombok.NonNull;

public record AddItemPetitionRequest(
        @NonNull
        @Min(1)
        Long cartId,

        @NonNull
        @Min(1)
        Long gameId,

        @NonNull
        @Min(1)
        Integer quantity
) {
}
