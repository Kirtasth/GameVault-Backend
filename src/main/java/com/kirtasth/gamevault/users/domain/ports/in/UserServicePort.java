package com.kirtasth.gamevault.users.domain.ports.in;

import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.NewUser;
import com.kirtasth.gamevault.users.domain.models.Role;
import com.kirtasth.gamevault.users.domain.models.User;
import com.kirtasth.gamevault.users.domain.models.UserCriteria;

import java.util.List;

public interface UserServicePort {

    Result<User> getUserById(Long id);

    Result<User> getUserByEmail(String email);

    Page<User> listUsersWithCriteria(UserCriteria criteria, PageRequest pageRequest);

    Result<User> saveUser(NewUser newUser);

    Result<Boolean> lockUserById(Long id, String reason);

    Result<Boolean> unlockUserById(Long id);

    Result<Boolean> deleteUserById(Long id);

    Result<Void> addRolesToUser(Long id, List<RoleEnum> roleEnums);

    Result<Boolean> removeRolesFromUser(Long id, List<RoleEnum> roleEnums);

    List<Role> getRolesByUserId(Long userId);

    Result<Boolean> canCreateGames(Long userId);

    Result<Long> getUserId(String email);
}
