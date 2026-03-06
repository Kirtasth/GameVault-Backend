package com.kirtasth.gamevault.cart.infrastructure.repositories.jpa;

import com.kirtasth.gamevault.cart.infrastructure.dtos.entities.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
}
