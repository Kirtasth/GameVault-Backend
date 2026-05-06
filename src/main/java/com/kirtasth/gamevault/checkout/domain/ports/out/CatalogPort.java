package com.kirtasth.gamevault.checkout.domain.ports.out;

import com.kirtasth.gamevault.catalog.domain.models.Game;

import java.util.List;

public interface CatalogPort {
    boolean isDeveloperOfGame(Long developerId, Long gameId);

    Game findById(Long gameId);

    List<Game> findGamesByIds(List<Long> gameIds);
}
