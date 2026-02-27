package com.kirtasth.gamevault.catalog.domain.models;

import com.kirtasth.gamevault.common.models.enums.GameStatusEnum;

public record NewGameStatus(
        Long gameId,
        GameStatusEnum status,
        String description
) {
}
