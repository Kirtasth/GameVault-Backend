package com.kirtasth.gamevault.checkout.infrastructure.repositories.jpa;

import com.kirtasth.gamevault.checkout.infrastructure.dtos.entities.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {
    
    @Query("SELECT DISTINCT oi.game.id FROM OrderItemEntity oi WHERE oi.order.userId = :userId AND oi.order.status = 'PAID'")
    List<Long> findPurchasedGameIdsByUserId(@Param("userId") Long userId);
}
