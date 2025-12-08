package com.kirtasth.gamevault.users.domain.ports.in;

import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.User;
import com.kirtasth.gamevault.users.domain.UserCriteria;

import java.util.List;

public interface UserServicePort {

    Result<User> getUserById(Long id);
    Result<User> getUserByEmail(String email);
    Result<List<User>> listUsersWithCriteria(UserCriteria criteria, PageRequest pageRequest);
    Result<User> saveUser(User user);
    Result<Boolean> lockUserById(Long id, String reason);
    Result<Boolean> unlockUserById(Long id);
    Result<Boolean> deleteUserById(Long id);
    Result<Boolean> addRolesToUser(Long id, List<RoleEnum> roleEnums);
    Result<Boolean> removeRolesFromUser(Long id, List<RoleEnum> roleEnums);
    Result<List<RoleEnum>> getUserByRoles(Long id);

}
