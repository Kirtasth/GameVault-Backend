package com.kirtasth.gamevault.users.infrastructure.specifications;

import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.RoleEntity;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserEntity;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserRoleEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class UserEntitySpecificationImpl implements UserEntitySpecification {

    @Override
    public Specification<UserEntity> containsUsername(String username) {
        return (root, query, criteriaBuilder) ->
                username == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("username")),
                        "%" + username.toLowerCase() + "%"
                );
    }

    @Override
    public Specification<UserEntity> containsEmail(String email) {
        return ((root, query, criteriaBuilder) ->
                email == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + email.toLowerCase() + "%"
                ));
    }

    @Override
    public Specification<UserEntity> isEmailVerified(Boolean emailVerified) {
        return ((root, query, criteriaBuilder) ->
                emailVerified == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("emailVerified"), emailVerified));
    }

    @Override
    public Specification<UserEntity> isAccountEnabled(Boolean accountEnabled) {
        return ((root, query, criteriaBuilder) ->
                accountEnabled == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("accountEnabled"), accountEnabled));
    }

    @Override
    public Specification<UserEntity> isAccountExpired(Boolean accountExpired) {
        return ((root, query, criteriaBuilder) ->
                accountExpired == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("accountExpired"), accountExpired));
    }

    @Override
    public Specification<UserEntity> isAccountLocked(Boolean accountLocked) {
        return ((root, query, criteriaBuilder) ->
                accountLocked == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("accountLocked"), accountLocked));
    }

    @Override
    public Specification<UserEntity> isCredentialsExpired(Boolean credentialsExpired) {
        return ((root, query, criteriaBuilder) ->
                credentialsExpired == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("credentialsExpired"), credentialsExpired));
    }

    @Override
    public Specification<UserEntity> lockedBefore(Instant time) {
        return ((root, query, criteriaBuilder) ->
                time == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThan(root.get("lockInstant"), time));
    }

    @Override
    public Specification<UserEntity> lockedAfter(Instant time) {
        return ((root, query, criteriaBuilder) ->
                time == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThan(root.get("lockInstant"), time));
    }

    @Override
    public Specification<UserEntity> createdBefore(Instant time) {
        return ((root, query, criteriaBuilder) ->
                time == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThan(root.get("createdAt"), time));
    }

    @Override
    public Specification<UserEntity> createdAfter(Instant time) {
        return ((root, query, criteriaBuilder) ->
                time == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThan(root.get("createdAt"), time));
    }

    @Override
    public Specification<UserEntity> updatedBefore(Instant time) {
        return (root, query, criteriaBuilder) ->
                time == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThan(root.get("updatedAt"), time);
    }

    @Override
    public Specification<UserEntity> updatedAfter(Instant time) {
        return ((root, query, criteriaBuilder) ->
                time == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThan(root.get("updatedAt"), time));
    }

    @Override
    public Specification<UserEntity> deletedBefore(Instant time) {
        return (root, query, criteriaBuilder) ->
                time == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.lessThan(root.get("deletedAt"), time);
    }

    @Override
    public Specification<UserEntity> deletedAfter(Instant time) {
        return ((root, query, criteriaBuilder) ->
                time == null
        ? criteriaBuilder.conjunction()
                : criteriaBuilder.greaterThan(root.get("deletedAt"), time));
    }

    @Override
    public Specification<UserEntity> containsAllRoles(List<RoleEnum> roleEnums) {
        return (root, query, criteriaBuilder) -> {
            if (roleEnums == null || roleEnums.isEmpty() || query == null){
                return criteriaBuilder.conjunction();
            }

            Join<UserEntity, UserRoleEntity> userRoles = root.join("userRoles");
            Join<UserRoleEntity, RoleEntity> roles = userRoles.join("role");

            query.where(roles.get("role").as(String.class).in(roleEnums.stream().map(RoleEnum::name).toList()));

            query.groupBy(root.get("id"));

            query.having(criteriaBuilder.equal(criteriaBuilder.count(root), (long) roleEnums.size()));

            return query.getRestriction();
        };
    }
}
