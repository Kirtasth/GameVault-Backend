package com.kirtasth.gamevault.catalog.infrastructure.repositories.jpa;

import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.GameStatusEntity;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.GameStatusKey;
import com.kirtasth.gamevault.common.models.enums.GameStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameStatusRepository extends JpaRepository<GameStatusEntity, GameStatusKey> {

    Optional<GameStatusEntity> findByStatus(GameStatusEnum status);
}
