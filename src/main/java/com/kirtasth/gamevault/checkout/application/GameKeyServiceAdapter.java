package com.kirtasth.gamevault.checkout.application;

import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.checkout.application.exceptions.GameKeyNotFoundException;
import com.kirtasth.gamevault.checkout.domain.models.GameKey;
import com.kirtasth.gamevault.checkout.domain.ports.in.ManageGameKeysUseCase;
import com.kirtasth.gamevault.checkout.domain.ports.out.CatalogPort;
import com.kirtasth.gamevault.checkout.domain.ports.out.GameKeyRepository;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.responses.PurchasedGameKeyResponse;
import com.kirtasth.gamevault.common.application.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameKeyServiceAdapter implements ManageGameKeysUseCase {

    private final GameKeyRepository gameKeyRepository;
    private final CatalogPort catalogPort;

    @Override
    @Transactional
    public void uploadKeys(Long developerId, Long gameId, List<String> keys) {
        validateOwnership(developerId, gameId);
        List<GameKey> gameKeys = keys.stream()
                .map(keyValue -> GameKey.builder()
                        .gameId(gameId)
                        .keyValue(keyValue)
                        .isUsed(false)
                        .build())
                .collect(Collectors.toList());
        gameKeyRepository.saveAll(gameKeys);
    }

    @Override
    @Transactional
    public void markKeyAsUsed(Long developerId, Long keyId) {
        GameKey gameKey = gameKeyRepository.findById(keyId)
                .orElseThrow(() -> new GameKeyNotFoundException("Key not found with id: " + keyId));
        validateOwnership(developerId, gameKey.getGameId());
        
        gameKey.setIsUsed(true);
        gameKey.setUsedAt(Instant.now());
        gameKeyRepository.save(gameKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameKey> getKeysByGameId(Long developerId, Long gameId) {
        validateOwnership(developerId, gameId);
        return gameKeyRepository.findByGameId(gameId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchasedGameKeyResponse> getPurchasedKeys(Long userId) {
        List<GameKey> purchasedKeys = gameKeyRepository.findPurchasedKeysByUserId(userId);
        
        if (purchasedKeys.isEmpty()) {
            return List.of();
        }

        List<Long> gameIds = purchasedKeys.stream()
                .map(GameKey::getGameId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Game> gamesMap = catalogPort.findGamesByIds(gameIds).stream()
                .collect(Collectors.toMap(Game::id, Function.identity()));

        return purchasedKeys.stream()
                .map(key -> {
                    Game game = gamesMap.get(key.getGameId());
                    return PurchasedGameKeyResponse.builder()
                            .gameId(key.getGameId())
                            .gameTitle(game != null ? game.title() : "Unknown Game")
                            .imageUrl(game != null ? game.imageUrl() : null)
                            .keyValue(key.getKeyValue())
                            .purchasedAt(key.getUpdatedAt()) // Use updatedAt as a proxy for purchase time
                            .build();
                })
                .collect(Collectors.toList());
    }

    private void validateOwnership(Long developerId, Long gameId) {
        if (!catalogPort.isDeveloperOfGame(developerId, gameId)) {
            throw new ForbiddenException("You are not the developer of this game.");
        }
    }
}
