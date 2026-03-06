package com.kirtasth.gamevault.users.infrastructure.repositories;

import com.kirtasth.gamevault.common.infrastructure.PageMapper;
import com.kirtasth.gamevault.common.domain.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.domain.models.page.Page;
import com.kirtasth.gamevault.common.domain.models.page.PageRequest;
import com.kirtasth.gamevault.users.application.exception.RoleAssignmentException;
import com.kirtasth.gamevault.users.application.exception.RoleNotFoundException;
import com.kirtasth.gamevault.users.application.exception.UserAlreadyRegisteredException;
import com.kirtasth.gamevault.users.application.exception.UserNotFoundException;
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
    public User findUserById(Long id) throws UserNotFoundException {
        return this.userRepository.findById(id).map(userMapper::toUser).orElseThrow(
                () -> new UserNotFoundException(id.toString())
        );
    }

    @Override
    public User findUserByEmail(String email) throws UserNotFoundException {
        return this.userRepository.findByEmail(email).map(userMapper::toUser).orElseThrow(
                () -> new UserNotFoundException(email)
        );
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
    public Role findRole(RoleEnum role) throws RoleNotFoundException {
        return this.roleRepository.findByRole(role).map(this.userMapper::toRole).orElseThrow(
                () -> new RoleNotFoundException(role)
        );
    }

    @Override
    public User saveUser(User user) throws UserAlreadyRegisteredException {
        try {
            return this.userMapper.toUser(this.userRepository.save(this.userMapper.toUserEntity(user)));
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyRegisteredException();
        }
    }

    @Override
    public User addRolesToUser(Long id, List<RoleEnum> roleEnums) throws RoleNotFoundException, RoleAssignmentException, UserNotFoundException {
        try {

            var roles = roleEnums.stream()
                    .map(role -> this.roleRepository.findByRole(role)
                            .orElseThrow(
                                    () -> new RoleNotFoundException(role)))
                    .toList();

            var user = this.userRepository.findById(id)
                    .orElseThrow(
                            () -> new UserNotFoundException(id.toString()));

            roles.forEach(
                    role -> {
                        var newUserRole = new UserRoleEntity();
                        newUserRole.setId(new UserRoleKey(id, role.getId()));
                        newUserRole.setUser(user);
                        newUserRole.setRole(role);

                        this.userRoleRepository.save(newUserRole);
                    }
            );

            return this.userRepository.findById(id)
                    .map(userMapper::toUser)
                    .orElseThrow(
                            () -> new UserNotFoundException(id.toString()));
        } catch (DataIntegrityViolationException e) {
            throw new RoleAssignmentException(id, roleEnums);
        }
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

}
