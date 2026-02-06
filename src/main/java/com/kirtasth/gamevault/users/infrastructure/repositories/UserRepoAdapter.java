package com.kirtasth.gamevault.users.infrastructure.repositories;

import com.kirtasth.gamevault.common.infrastructure.PageMapper;
import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.Role;
import com.kirtasth.gamevault.users.domain.models.User;
import com.kirtasth.gamevault.users.domain.models.UserCriteria;
import com.kirtasth.gamevault.users.domain.ports.out.UserRepoPort;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserEntity;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserRoleEntity;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserRoleKey;
import com.kirtasth.gamevault.users.infrastructure.mappers.UserMapper;
import com.kirtasth.gamevault.users.infrastructure.repositories.jpa.RoleRepository;
import com.kirtasth.gamevault.users.infrastructure.repositories.jpa.UserRepository;
import com.kirtasth.gamevault.users.infrastructure.repositories.jpa.UserRoleRepository;
import com.kirtasth.gamevault.users.infrastructure.specifications.UserEntitySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepoAdapter implements UserRepoPort {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;
    private final PageMapper pageMapper;
    private final UserEntitySpecification userEntitySpecification;

    @Override
    public Result<User> findUserById(Long id) {
        var dbUser = this.userRepository.findById(id).map(userMapper::toUser);

        if (dbUser.isEmpty()) {
            return new Result.Failure<>(
                    404,
                    "User with id: " + id + " not found.",
                    null,
                    null
            );
        }

        return new Result.Success<>(dbUser.get());
    }

    @Override
    public Result<User> findUserByEmail(String email) {
        var dbUser = this.userRepository.findByEmail(email)
                .map(userMapper::toUser);

        if (dbUser.isEmpty()) {
            return new Result.Failure<>(
                    404,
                    "User with email: " + email + " not found.",
                    null,
                    null
            );
        }

        return new Result.Success<>(dbUser.get());
    }

    @Override
    public Page<User> findAllUsersWithCriteria(UserCriteria criteria, PageRequest pageRequest) {
        var pageable = this.pageMapper.toSpring(pageRequest);

        Specification<UserEntity> spec = Specification.allOf(
                this.userEntitySpecification.containsUsername(criteria.getUsername()),
                this.userEntitySpecification.containsEmail(criteria.getEmail()),
                this.userEntitySpecification.isEmailVerified(criteria.getEmailVerified()),
                this.userEntitySpecification.isAccountEnabled(criteria.getAccountEnabled()),
                this.userEntitySpecification.isAccountExpired(criteria.getAccountExpired()),
                this.userEntitySpecification.isAccountLocked(criteria.getAccountLocked()),
                this.userEntitySpecification.isCredentialsExpired(criteria.getCredentialsExpired()),
                this.userEntitySpecification.lockedBefore(criteria.getLockedBefore()),
                this.userEntitySpecification.lockedAfter(criteria.getLockedAfter()),
                this.userEntitySpecification.createdBefore(criteria.getCreatedBefore()),
                this.userEntitySpecification.createdAfter(criteria.getCreatedAfter()),
                this.userEntitySpecification.updatedBefore(criteria.getUpdatedBefore()),
                this.userEntitySpecification.updatedAfter(criteria.getUpdatedAfter()),
                this.userEntitySpecification.deletedBefore(criteria.getDeletedBefore()),
                this.userEntitySpecification.deletedAfter(criteria.getDeletedAfter()),
                this.userEntitySpecification.containsAllRoles(criteria.getRoleEnums())
        );

        var page = this.userRepository.findAll(spec, pageable).map(userMapper::toUser);
        return this.pageMapper.toDomain(page);
    }

    @Override
    public Result<User> saveUser(User user) {
        try {
            var dbUser = this.userRepository.save(this.userMapper.toUserEntity(user));

            var defaultRoles = List.of(RoleEnum.USER);

            var res = this.addRolesToUser(dbUser.getId(), defaultRoles);

            if (res instanceof Result.Failure) {
                return new Result.Failure<>(
                        400,
                        "Error saving user.",
                        Map.of("roles", "Error adding default roles to user."),
                        null
                );
            }

            return new Result.Success<>(this.userMapper.toUser(dbUser));
        } catch (DataIntegrityViolationException e) {
            return new Result.Failure<>(
                    400,
                    "Error saving user.",
                    null,
                    e
            );
        }
    }

    @Override
    public Result<Boolean> lockUserById(Long id, String reason) {

        try {
            var locked = this.userRepository.lockUserById(id, reason);

            if (!locked) {
                return new Result.Failure<>(
                        400,
                        "Error locking user with id: " + id + ".",
                        null,
                        null
                );
            }

            return new Result.Success<>(true);
        } catch (DataIntegrityViolationException e) {
            return new Result.Failure<>(
                    400,
                    "Error locking user with id: " + id + ".",
                    null,
                    e
            );
        }
    }

    @Override
    public Result<Boolean> unlockUserById(Long id) {

        try {
            var unlocked = this.userRepository.unlockUserById(id);

            if (!unlocked) {
                return new Result.Failure<>(
                        400,
                        "Error unlocking user with id: " + id + ".",
                        null,
                        null
                );
            }

            return new Result.Success<>(true);
        } catch (DataIntegrityViolationException e) {
            return new Result.Failure<>(
                    400,
                    "Error unlocking user with id: " + id + ".",
                    null,
                    e
            );
        }
    }

    @Override
    public Result<Boolean> deleteUserById(Long id) {

        try {
            var deleted = this.userRepository.softDeleteUserById(id);

            if (!deleted) {
                return new Result.Failure<>(
                        400,
                        "Error deleting user with id: " + id + ".",
                        null,
                        null
                );
            }

            return new Result.Success<>(true);
        } catch (DataIntegrityViolationException e) {
            return new Result.Failure<>(
                    400,
                    "Error deleting user with id: " + id + ".",
                    null,
                    e
            );
        }
    }

    @Override
    public Result<Boolean> addRolesToUser(Long id, List<RoleEnum> roleEnums) {
        try {

            var roleEntities = roleEnums.stream()
                    .map(role -> this.roleRepository.findByRole(role).stream()
                            .findFirst().orElse(null))
                    .toList();

            if (roleEntities.isEmpty() || roleEntities.contains(null)) {
                return new Result.Failure<>(
                        400,
                        "Error adding roles to user with id: " + id + ".",
                        Map.of("roles", "One or more roles where not found."),
                        null
                );
            }

            var userEntity = this.userRepository.findById(id);

            if (userEntity.isEmpty()) {
                return new Result.Failure<>(
                        400,
                        "Error adding roles to user with id: " + id + ".",
                        Map.of("user", "User was not found."),
                        null
                );
            }


            roleEntities.forEach(
                    role -> {
                        var newUserRole = new UserRoleEntity();
                        newUserRole.setId(new UserRoleKey(id, role.getId()));
                        newUserRole.setUser(userEntity.get());
                        newUserRole.setRole(role);

                        this.userRoleRepository.save(newUserRole);
                    }
            );
        } catch (Exception e) {
            return new Result.Failure<>(
                    400,
                    "Error adding roles to user with id: " + id + ".",
                    null,
                    e
            );
        }

        return new Result.Success<>(true);
    }

    @Override
    public Result<Boolean> removeRolesFromUser(Long id, List<RoleEnum> roleEnums) {
        return null;
    }

    @Override
    public List<Role> findRolesByUserId(Long userId) {
        var userRoleList = this.userRoleRepository.findAllByUserId(userId);
        return userRoleList.stream()
                .map(userRole ->
                        this.roleRepository.findById(userRole.getRole().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this.userMapper::toRole)
                .toList();
    }

    @Override
    public User getReference(Long id) {
        return this.userMapper.toUser(this.userRepository.getReferenceById(id));
    }
}
