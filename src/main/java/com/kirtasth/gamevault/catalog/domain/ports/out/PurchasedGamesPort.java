package com.kirtasth.gamevault.catalog.domain.ports.out;

import java.util.List;

public interface PurchasedGamesPort {
    List<Long> getPurchasedGameIds(Long userId);
}
