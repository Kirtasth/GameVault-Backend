package com.kirtasth.gamevault.catalog.infrastructure.adapters;

import com.kirtasth.gamevault.catalog.domain.ports.out.PurchasedGamesPort;
import com.kirtasth.gamevault.checkout.infrastructure.repositories.jpa.OrderItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PurchasedGamesAdapter implements PurchasedGamesPort {

    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public List<Long> getPurchasedGameIds(Long userId) {
        return orderItemJpaRepository.findPurchasedGameIdsByUserId(userId);
    }
}
