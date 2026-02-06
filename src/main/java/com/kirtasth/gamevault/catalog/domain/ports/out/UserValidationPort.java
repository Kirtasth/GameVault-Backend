package com.kirtasth.gamevault.catalog.domain.ports.out;

import com.kirtasth.gamevault.common.models.util.Result;

public interface UserValidationPort {

    Result<Boolean> canCreateGames(Long userId);
    Result<Boolean> isDeveloper(String email);

    Result<Long> getUserId(String email);
}
