package com.kirtasth.gamevault.checkout.infrastructure.mappers;

import com.kirtasth.gamevault.checkout.infrastructure.dtos.entities.GameKeyEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class GameKeyEntityResolver {

    @PersistenceContext
    private EntityManager entityManager;

    @Named("gameKeyIdToEntity")
    public GameKeyEntity resolveGameKeyEntity(Long gameKeyId) {
        if (gameKeyId == null) {
            return null;
        }

        return entityManager.getReference(GameKeyEntity.class, gameKeyId);
    }
}
