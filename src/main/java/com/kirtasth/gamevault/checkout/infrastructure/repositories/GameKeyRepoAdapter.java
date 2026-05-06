package com.kirtasth.gamevault.checkout.infrastructure.repositories;

import com.kirtasth.gamevault.checkout.domain.models.GameKey;
import com.kirtasth.gamevault.checkout.domain.ports.out.GameKeyRepository;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.entities.GameKeyEntity;
import com.kirtasth.gamevault.checkout.infrastructure.mappers.GameKeyMapper;
import com.kirtasth.gamevault.checkout.infrastructure.repositories.jpa.GameKeyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GameKeyRepoAdapter implements GameKeyRepository {

    private final GameKeyJpaRepository gameKeyJpaRepository;
    private final GameKeyMapper gameKeyMapper;

    @Override
    public void saveAll(List<GameKey> gameKeys) {
        List<GameKeyEntity> entities = gameKeys.stream()
                .map(gameKeyMapper::toEntity)
                .collect(Collectors.toList());
        gameKeyJpaRepository.saveAll(entities);
    }

    @Override
    public GameKey save(GameKey gameKey) {
        GameKeyEntity entity = gameKeyMapper.toEntity(gameKey);
        return gameKeyMapper.toDomain(gameKeyJpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        gameKeyJpaRepository.deleteById(id);
    }

    @Override
    public List<GameKey> findByGameId(Long gameId) {
        return gameKeyJpaRepository.findByGameId(gameId).stream()
                .map(gameKeyMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GameKey> findById(Long id) {
        return gameKeyJpaRepository.findById(id)
                .map(gameKeyMapper::toDomain);
    }

    @Override
    public long countAvailableKeysByGameId(Long gameId) {
        return gameKeyJpaRepository.countAvailableByGameId(gameId, Instant.now());
    }

    @Override
    public Optional<GameKey> findRandomUnusedKeyByGameId(Long gameId) {
        return gameKeyJpaRepository.findRandomUnusedKeyByGameId(gameId, Instant.now())
                .map(gameKeyMapper::toDomain);
    }

    @Override
    public List<GameKey> findAvailableKeysByGameId(Long gameId, int limit) {
        return gameKeyJpaRepository.findAvailableKeysByGameId(gameId, limit, Instant.now()).stream()
                .map(gameKeyMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameKey> findReservedKeysByUserId(Long userId) {
        return gameKeyJpaRepository.findByReservedByUserId(userId).stream()
                .map(gameKeyMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameKey> findPurchasedKeysByUserId(Long userId) {
        return gameKeyJpaRepository.findByPurchasedByUserId(userId).stream()
                .map(gameKeyMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void releaseExpiredReservations(Instant now) {
        gameKeyJpaRepository.releaseExpiredReservations(now);
    }
}
