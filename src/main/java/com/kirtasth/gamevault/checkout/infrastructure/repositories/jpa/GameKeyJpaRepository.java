package com.kirtasth.gamevault.checkout.infrastructure.repositories.jpa;

import com.kirtasth.gamevault.checkout.infrastructure.dtos.entities.GameKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameKeyJpaRepository extends JpaRepository<GameKeyEntity, Long> {
    List<GameKeyEntity> findByGameId(Long gameId);
    
    @Query(value = "SELECT * FROM checkout.game_keys WHERE game_id = :gameId AND used_at IS NULL ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<GameKeyEntity> findRandomUnusedKeyByGameId(@Param("gameId") Long gameId);

    long countByGameIdAndUsedAtIsNull(Long gameId);

    Optional<GameKeyEntity> findByOrderItemId(Long orderItemId);
}
