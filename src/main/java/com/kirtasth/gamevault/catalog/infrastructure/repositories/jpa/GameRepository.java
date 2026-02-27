package com.kirtasth.gamevault.catalog.infrastructure.repositories.jpa;

import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface GameRepository extends JpaRepository<GameEntity, Long>,
        JpaSpecificationExecutor<GameEntity> {

    @Modifying
    @Transactional
    @Query("UPDATE GameEntity g SET g.imageUrl = :imageUrl WHERE g.id = :gameId")
    void updateImageUrl(@Param("gameId") Long gameId, @Param("imageUrl") String imageUrl);
}
