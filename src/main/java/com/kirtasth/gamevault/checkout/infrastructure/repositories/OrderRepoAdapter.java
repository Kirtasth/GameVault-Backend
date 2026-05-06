package com.kirtasth.gamevault.checkout.infrastructure.repositories;

import com.kirtasth.gamevault.checkout.domain.models.Order;
import com.kirtasth.gamevault.checkout.domain.ports.out.OrderRepository;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.entities.OrderEntity;
import com.kirtasth.gamevault.checkout.infrastructure.mappers.OrderMapper;
import com.kirtasth.gamevault.checkout.infrastructure.repositories.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderRepoAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderMapper orderMapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = orderMapper.toEntity(order);
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> item.setOrder(entity));
        }
        return orderMapper.toDomain(orderJpaRepository.save(entity));
    }

    @Override
    public Optional<Order> findByStripeSessionId(String stripeSessionId) {
        return orderJpaRepository.findByStripeSessionId(stripeSessionId)
                .map(orderMapper::toDomain);
    }
}
