package com.kirtasth.gamevault.catalog.infrastructure.mappers;

import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.DeveloperEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class DeveloperEntityResolver {

    @PersistenceContext
    private EntityManager entityManager;

    @Named("developerIdToEntity")
    public DeveloperEntity resolveDeveloperEntity(Long developerId) {
        if (developerId == null) {
            return null;
        }

        return entityManager.getReference(DeveloperEntity.class, developerId);
    }
}
