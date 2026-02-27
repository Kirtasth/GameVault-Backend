package com.kirtasth.gamevault.catalog.domain.ports.out;

import com.kirtasth.gamevault.common.models.enums.RoleEnum;

import java.util.List;

public interface UserValidationPort {

    boolean canCreateGames(Long userId);

    Long getUserId(String email);

    void addRoles(Long userId, List<RoleEnum> roles);
}
