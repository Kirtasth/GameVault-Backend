package com.kirtasth.gamevault.cart.infrastructure.adapters;

import com.kirtasth.gamevault.cart.domain.ports.out.CatalogPort;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.catalog.domain.ports.in.GameServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogAdapter implements CatalogPort {

    private final GameServicePort gameServicePort;

    @Override
    public Game getGame(Long id) {
        return gameServicePort.findById(id);
    }
}
