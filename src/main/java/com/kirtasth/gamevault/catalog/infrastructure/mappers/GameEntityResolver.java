package com.kirtasth.gamevault.catalog.infrastructure.mappers;

import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.GameEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class GameEntityResolver {

    @PersistenceContext
    private EntityManager entityManager;

    @Named("gameIdToEntity")
    public GameEntity resolveGameEntity(Long gameId) {
        if (gameId == null) {
            return null;
        }

        return entityManager.getReference(GameEntity.class, gameId);
    }
}
