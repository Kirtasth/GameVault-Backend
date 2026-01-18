package com.kirtasth.gamevault.users.application.services;

import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.*;
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
        return userRepo.findUserById(id);
    }

    @Override
    public Result<User> getUserByEmail(String email) {
        return this.userRepo.findUserByEmail(email);
    }

    @Override
    public Page<User> listUsersWithCriteria(UserCriteria criteria, PageRequest pageRequest) {

        return this.userRepo.findAllUsersWithCriteria(criteria, pageRequest);
    }

    @Override
    public Result<User> saveUser(NewUser newUser) {

        var user = new User();
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        user.setPassword(newUser.getPassword());
        user.setAvatarUrl(newUser.getAvatarUrl());
        user.setBio(newUser.getBio());
        user.setAccountEnabled(true);
        user.setAccountExpired(false);
        user.setAccountLocked(false);
        user.setCredentialsExpired(false);

        return userRepo.saveUser(user);
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
    public List<Role> getRolesByUserId(Long userId) {
        return this.userRepo.findRolesByUserId(userId);
    }
}
