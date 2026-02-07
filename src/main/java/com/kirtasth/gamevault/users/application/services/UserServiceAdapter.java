package com.kirtasth.gamevault.users.application.services;

import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.NewUser;
import com.kirtasth.gamevault.users.domain.models.Role;
import com.kirtasth.gamevault.users.domain.models.User;
import com.kirtasth.gamevault.users.domain.models.UserCriteria;
import com.kirtasth.gamevault.users.domain.ports.in.UserServicePort;
import com.kirtasth.gamevault.users.domain.ports.out.UserRepoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    public Result<Void> addRolesToUser(Long id, List<RoleEnum> roleEnums) {
        return this.userRepo.addRolesToUser(id, roleEnums);
    }

    @Override
    public Result<Boolean> removeRolesFromUser(Long id, List<RoleEnum> roleEnums) {
        return null;
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return this.userRepo.findRolesByUserId(userId);
    }

    @Override
    public Result<Boolean> canCreateGames(Long userId) {
        var roles = this.getRolesByUserId(userId);

        return new Result.Success<>(roles.stream()
                .map(Role::getRole)
                .anyMatch(
                        role -> role == RoleEnum.ADMIN || role == RoleEnum.DEVELOPER
                ));
    }

    @Override
    public Result<Long> getUserId(String email) {
        var userRes = this.userRepo.findUserByEmail(email);

        if (userRes instanceof Result.Failure<User>(
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(errorCode, errorMsg, errorDetails, exception);
        }

        var user = ((Result.Success<User>) userRes).data();

        return new Result.Success<>(user.getId());
    }

}
