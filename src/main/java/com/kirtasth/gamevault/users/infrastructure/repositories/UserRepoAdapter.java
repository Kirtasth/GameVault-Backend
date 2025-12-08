package com.kirtasth.gamevault.users.infrastructure.repositories;

import com.kirtasth.gamevault.common.infrastructure.PageMapper;
import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.User;
import com.kirtasth.gamevault.users.domain.UserCriteria;
import com.kirtasth.gamevault.users.domain.ports.out.UserRepoPort;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserEntity;
import com.kirtasth.gamevault.users.infrastructure.mappers.UserMapper;
import com.kirtasth.gamevault.users.infrastructure.specifications.UserEntitySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepoAdapter implements UserRepoPort {

    private final UserRepository userRepository;
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
        var dbUser = this.userRepository.findByEmail(email).map(userMapper::toUser);

        if (dbUser.isEmpty()){
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
        try{
            var dbUser = this.userRepository.save(this.userMapper.toUserEntity(user));

            return new Result.Success<>(this.userMapper.toUser(dbUser));
        } catch (DataIntegrityViolationException e){
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
    public Result<Boolean> addRolesToUser(Long id, List<RoleEnum> roleEnums) {
        return null;
    }

    @Override
    public Result<Boolean> removeRolesFromUser(Long id, List<RoleEnum> roleEnums) {
        return null;
    }

    @Override
    public Result<List<RoleEnum>> findUserByRoles(Long id) {
        return null;
    }
}
