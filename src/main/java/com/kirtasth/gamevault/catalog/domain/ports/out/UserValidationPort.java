package com.kirtasth.gamevault.catalog.domain.ports.out;

import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.models.util.Result;

import java.util.List;

public interface UserValidationPort {

    Result<Boolean> canCreateGames(Long userId);

    Result<Long> getUserId(String email);

    Result<Void> addRoles(Long userId, List<RoleEnum> roles);
}
