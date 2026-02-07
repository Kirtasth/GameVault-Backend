package com.kirtasth.gamevault.catalog.infrastructure.adapters;

import com.kirtasth.gamevault.catalog.domain.ports.out.UserValidationPort;
import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.ports.in.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserValidationAdapter implements UserValidationPort {

    private final UserServicePort userService;

    @Override
    public Result<Boolean> canCreateGames(Long userId) {

        return this.userService.canCreateGames(userId);
    }

    @Override
    public Result<Long> getUserId(String email) {
        return this.userService.getUserId(email);
    }

    @Override
    public Result<Void> addRoles(Long userId, List<RoleEnum> roles) {
        return this.userService.addRolesToUser(userId, roles);
    }
}
