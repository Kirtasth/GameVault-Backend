package com.kirtasth.gamevault.checkout.infrastructure.adapters;

import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.catalog.domain.ports.in.GameServicePort;
import com.kirtasth.gamevault.checkout.domain.ports.out.CatalogPort;
import com.kirtasth.gamevault.common.domain.models.page.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("checkoutCatalogAdapter")
@RequiredArgsConstructor
public class CatalogAdapter implements CatalogPort {

    private final GameServicePort gameServicePort;

    @Override
    public boolean isDeveloperOfGame(Long developerId, Long gameId) {
        return gameServicePort.isDeveloperOfGame(developerId, gameId);
    }

    @Override
    public Game findById(Long gameId) {
        return gameServicePort.findById(gameId);
    }

    @Override
    public List<Game> findGamesByIds(List<Long> gameIds) {
        return gameServicePort.listCustomGames(gameIds, PageRequest.of(0, gameIds.size())).content();
    }
}
