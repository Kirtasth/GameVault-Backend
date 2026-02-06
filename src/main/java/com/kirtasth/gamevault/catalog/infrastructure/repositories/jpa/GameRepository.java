package com.kirtasth.gamevault.catalog.infrastructure.repositories.jpa;

import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GameRepository extends JpaRepository<GameEntity, Long>,
        JpaSpecificationExecutor<GameEntity> {
}
