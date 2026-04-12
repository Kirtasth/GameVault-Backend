package com.kirtasth.gamevault.checkout.infrastructure.repositories.jpa;

import com.kirtasth.gamevault.checkout.infrastructure.dtos.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByStripeSessionId(String stripeSessionId);
}
