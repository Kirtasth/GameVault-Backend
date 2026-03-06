package com.kirtasth.gamevault.users.domain.ports.in;

import com.kirtasth.gamevault.common.domain.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.domain.models.page.Page;
import com.kirtasth.gamevault.common.domain.models.page.PageRequest;
import com.kirtasth.gamevault.users.domain.models.NewUser;
import com.kirtasth.gamevault.users.domain.models.Role;
import com.kirtasth.gamevault.users.domain.models.UpdatedUser;
import com.kirtasth.gamevault.users.domain.models.User;
import com.kirtasth.gamevault.users.domain.models.UserCriteria;

import java.util.List;

public interface UserServicePort {

    User getUserById(Long id);

    User getUserByEmail(String email);

    Page<User> listUsersWithCriteria(UserCriteria criteria, PageRequest pageRequest);

    User saveUser(NewUser newUser);

    User updateUser(Long id, UpdatedUser updatedUser);

    User addRolesToUser(Long id, List<RoleEnum> roleEnums);

    List<Role> getRolesByUserId(Long userId);

    boolean canCreateGames(Long userId);

    Long getUserId(String email);
}
