package com.kirtasth.gamevault.users.infrastructure.mappers;

import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class UserEntityResolver {

    @PersistenceContext
    private EntityManager entityManager;

    @Named("userIdToEntity")
    public UserEntity resolveUserEntity(Long userId) {
        if (userId == null) {
            return null;
        }

        return entityManager.getReference(UserEntity.class, userId);
    }
}
