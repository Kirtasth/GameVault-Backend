package com.kirtasth.gamevault.users.domain.ports.out;

import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.User;
import com.kirtasth.gamevault.users.domain.UserCriteria;

import java.util.List;

public interface UserRepoPort {

    Result<User> findUserById(Long id);
    Result<User> findUserByEmail(String email);
    Page<User> findAllUsersWithCriteria(UserCriteria criteria, PageRequest pageRequest);
    Result<User> saveUser(User user);
    Result<Boolean> lockUserById(Long id, String reason);
    Result<Boolean> unlockUserById(Long id);
    Result<Boolean> deleteUserById(Long id);
    Result<Boolean> addRolesToUser(Long id, List<RoleEnum> roleEnums);
    Result<Boolean> removeRolesFromUser(Long id, List<RoleEnum> roleEnums);
    Result<List<RoleEnum>> findUserByRoles(Long id);
}
