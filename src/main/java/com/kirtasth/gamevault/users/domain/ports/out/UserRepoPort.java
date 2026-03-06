package com.kirtasth.gamevault.users.domain.ports.out;

import com.kirtasth.gamevault.common.domain.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.domain.models.page.Page;
import com.kirtasth.gamevault.common.domain.models.page.PageRequest;
import com.kirtasth.gamevault.users.application.exception.RoleNotFoundException;
import com.kirtasth.gamevault.users.application.exception.UserAlreadyRegisteredException;
import com.kirtasth.gamevault.users.application.exception.UserNotFoundException;
import com.kirtasth.gamevault.users.domain.models.Role;
import com.kirtasth.gamevault.users.domain.models.User;
import com.kirtasth.gamevault.users.domain.models.UserCriteria;

import java.util.List;

public interface UserRepoPort {

    User findUserById(Long id) throws UserNotFoundException;

    User findUserByEmail(String email) throws UserNotFoundException;

    Page<User> findAllUsersWithCriteria(UserCriteria criteria, PageRequest pageRequest);

    Role findRole(RoleEnum role) throws RoleNotFoundException;

    User saveUser(User user) throws UserAlreadyRegisteredException;

    User addRolesToUser(Long id, List<RoleEnum> roleEnums) throws RoleNotFoundException;

    List<Role> findRolesByUserId(Long userId);
}
