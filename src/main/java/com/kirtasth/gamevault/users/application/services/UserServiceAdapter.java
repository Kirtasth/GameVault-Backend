package com.kirtasth.gamevault.users.application.services;

import com.kirtasth.gamevault.common.domain.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.domain.models.page.Page;
import com.kirtasth.gamevault.common.domain.models.page.PageRequest;
import com.kirtasth.gamevault.users.domain.models.NewUser;
import com.kirtasth.gamevault.users.domain.models.Role;
import com.kirtasth.gamevault.users.domain.models.UpdatedUser;
import com.kirtasth.gamevault.users.domain.models.User;
import com.kirtasth.gamevault.users.domain.models.UserCriteria;
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
    public User getUserById(Long id) {
        return userRepo.findUserById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return this.userRepo.findUserByEmail(email);
    }

    @Override
    public Page<User> listUsersWithCriteria(UserCriteria criteria, PageRequest pageRequest) {
        return this.userRepo.findAllUsersWithCriteria(criteria, pageRequest);
    }

    @Override
    public User saveUser(NewUser newUser) {

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

        var userRole = this.userRepo.findRole(RoleEnum.USER);
        user.setRoles(List.of(userRole));


        return userRepo.saveUser(user);
    }

    @Override
    public User updateUser(Long id, UpdatedUser updatedUser) {
        var user = this.userRepo.findUserById(id);

        if (updatedUser.getUsername() != null) {
            user.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null) {
            user.setPassword(updatedUser.getPassword());
        }
        if (updatedUser.getAvatarUrl() != null) {
            user.setAvatarUrl(updatedUser.getAvatarUrl());
        }
        if (updatedUser.getBio() != null) {
            user.setBio(updatedUser.getBio());
        }

        return this.userRepo.saveUser(user);
    }

    @Override
    public User addRolesToUser(Long id, List<RoleEnum> roleEnums) {
        return this.userRepo.addRolesToUser(id, roleEnums);
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return this.userRepo.findRolesByUserId(userId);
    }

    @Override
    public boolean canCreateGames(Long userId) {
        var roles = this.getRolesByUserId(userId);

        return roles.stream()
                .map(Role::getRole)
                .anyMatch(
                        role -> role == RoleEnum.ADMIN || role == RoleEnum.DEVELOPER);
    }

    @Override
    public Long getUserId(String email) {
        return this.userRepo.findUserByEmail(email).getId();
    }

}
