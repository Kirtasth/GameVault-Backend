package com.kirtasth.gamevault.cart.infrastructure.repositories.jpa;

import com.kirtasth.gamevault.cart.infrastructure.dtos.entities.ShoppingCartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCartEntity, Long> {


    @Query("SELECT c FROM ShoppingCartEntity c WHERE c.userId = :userId AND c.status = 'OPENED'")
    Optional<ShoppingCartEntity> findFirstOpenedByUserId(Long userId);
}
