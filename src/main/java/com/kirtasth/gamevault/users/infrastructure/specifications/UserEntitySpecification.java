package com.kirtasth.gamevault.users.infrastructure.specifications;

import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;


public interface UserEntitySpecification {

    Specification<UserEntity> containsUsername(String username);
    Specification<UserEntity> containsEmail(String email);
    Specification<UserEntity> isEmailVerified(Boolean emailVerified);
    Specification<UserEntity> isAccountEnabled(Boolean accountEnabled);
    Specification<UserEntity> isAccountExpired(Boolean accountExpired);
    Specification<UserEntity> isAccountLocked(Boolean accountLocked);
    Specification<UserEntity> isCredentialsExpired(Boolean credentialsExpired);
    Specification<UserEntity> lockedBefore(Instant time);
    Specification<UserEntity> lockedAfter(Instant time);
    Specification<UserEntity> createdBefore(Instant time);
    Specification<UserEntity> createdAfter(Instant time);
    Specification<UserEntity> updatedBefore(Instant time);
    Specification<UserEntity> updatedAfter(Instant time);
    Specification<UserEntity> deletedBefore(Instant time);
    Specification<UserEntity> deletedAfter(Instant time);
    Specification<UserEntity> containsAllRoles(List<RoleEnum> roleEnums);
}
