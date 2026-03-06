package com.kirtasth.gamevault.catalog.infrastructure.dtos.requests;

import jakarta.validation.constraints.Min;
import lombok.NonNull;

import java.util.List;

public record CustomGameListRequest(

        @NonNull
        List<@Min(0) Long> ids
) {
}
