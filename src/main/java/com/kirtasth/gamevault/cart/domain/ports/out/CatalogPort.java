package com.kirtasth.gamevault.cart.domain.ports.out;

import com.kirtasth.gamevault.catalog.domain.models.Game;

public interface CatalogPort {
    Game getGame(Long id);
}
