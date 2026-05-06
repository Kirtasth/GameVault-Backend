package com.kirtasth.gamevault.checkout.domain.ports.out;

import com.kirtasth.gamevault.checkout.domain.models.GameKey;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface GameKeyRepository {
    void saveAll(List<GameKey> gameKeys);
    GameKey save(GameKey gameKey);
    void deleteById(Long id);
    List<GameKey> findByGameId(Long gameId);
    Optional<GameKey> findById(Long id);
    long countAvailableKeysByGameId(Long gameId);
    Optional<GameKey> findRandomUnusedKeyByGameId(Long gameId);
    List<GameKey> findAvailableKeysByGameId(Long gameId, int limit);
    List<GameKey> findReservedKeysByUserId(Long userId);
    List<GameKey> findPurchasedKeysByUserId(Long userId);
    void releaseExpiredReservations(Instant now);
}
