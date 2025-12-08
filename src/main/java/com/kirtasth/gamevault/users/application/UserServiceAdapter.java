package com.kirtasth.gamevault.users.application;

import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.User;
import com.kirtasth.gamevault.users.domain.UserCriteria;
import com.kirtasth.gamevault.users.domain.ports.in.UserServicePort;
import com.kirtasth.gamevault.users.domain.ports.out.UserRepoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceAdapter implements UserServicePort {

    private final UserRepoPort userRepo;

    @Override
    public Result<User> getUserById(Long id) {
        return null;
    }

    @Override
    public Result<User> getUserByEmail(String email) {
        return null;
    }

    @Override
    public Result<List<User>> listUsersWithCriteria(UserCriteria criteria, PageRequest pageRequest) {
        return null;
    }

    @Override
    public Result<User> saveUser(User user) {
        return null;
    }

    @Override
    public Result<Boolean> lockUserById(Long id, String reason) {
        return null;
    }

    @Override
    public Result<Boolean> unlockUserById(Long id) {
        return null;
    }

    @Override
    public Result<Boolean> deleteUserById(Long id) {
        return null;
    }

    @Override
    public Result<Boolean> addRolesToUser(Long id, List<RoleEnum> roleEnums) {
        return null;
    }

    @Override
    public Result<Boolean> removeRolesFromUser(Long id, List<RoleEnum> roleEnums) {
        return null;
    }

    @Override
    public Result<List<RoleEnum>> getUserByRoles(Long id) {
        return null;
    }
}
