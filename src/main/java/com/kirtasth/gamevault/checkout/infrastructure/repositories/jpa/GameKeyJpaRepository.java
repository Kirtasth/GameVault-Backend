package com.kirtasth.gamevault.checkout.infrastructure.repositories.jpa;

import com.kirtasth.gamevault.checkout.infrastructure.dtos.entities.GameKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface GameKeyJpaRepository extends JpaRepository<GameKeyEntity, Long> {
    List<GameKeyEntity> findByGameId(Long gameId);

    @Query(value = "SELECT * FROM checkout.game_keys WHERE game_id = :gameId " +
            "AND purchased_by_user_id IS NULL " +
            "AND (reserved_by_user_id IS NULL OR reserved_deadline < :now) " +
            "ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<GameKeyEntity> findRandomUnusedKeyByGameId(@Param("gameId") Long gameId, @Param("now") Instant now);

    @Query(value = "SELECT * FROM checkout.game_keys WHERE game_id = :gameId " +
            "AND purchased_by_user_id IS NULL " +
            "AND (reserved_by_user_id IS NULL OR reserved_deadline < :now) " +
            "LIMIT :limit", nativeQuery = true)
    List<GameKeyEntity> findAvailableKeysByGameId(@Param("gameId") Long gameId, @Param("limit") int limit, @Param("now") Instant now);

    @Query("SELECT COUNT(g) FROM GameKeyEntity g WHERE g.game.id = :gameId " +
            "AND g.purchasedByUserId IS NULL " +
            "AND (g.reservedByUserId IS NULL OR g.reservedDeadline < :now)")
    long countAvailableByGameId(@Param("gameId") Long gameId, @Param("now") Instant now);

    List<GameKeyEntity> findByReservedByUserId(Long userId);

    List<GameKeyEntity> findByPurchasedByUserId(Long userId);

    @Modifying
    @Query("UPDATE GameKeyEntity g SET g.reservedByUserId = NULL, g.reservedDeadline = NULL " +
            "WHERE g.reservedDeadline < :now AND g.purchasedByUserId IS NULL")
    void releaseExpiredReservations(@Param("now") Instant now);
}
