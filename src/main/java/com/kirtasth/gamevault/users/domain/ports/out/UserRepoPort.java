package com.kirtasth.gamevault.users.domain.ports.out;

import com.kirtasth.gamevault.common.domain.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.domain.models.page.Page;
import com.kirtasth.gamevault.common.domain.models.page.PageRequest;
import com.kirtasth.gamevault.users.domain.models.Role;
import com.kirtasth.gamevault.users.domain.models.User;
import com.kirtasth.gamevault.users.domain.models.UserCriteria;

import java.util.List;

public interface UserRepoPort {

    User findUserById(Long id);

    User findUserByEmail(String email);

    Page<User> findAllUsersWithCriteria(UserCriteria criteria, PageRequest pageRequest);

    Role findRole(RoleEnum role);

    User saveUser(User user);

    User addRolesToUser(Long id, List<RoleEnum> roleEnums);

    List<Role> findRolesByUserId(Long userId);
}
