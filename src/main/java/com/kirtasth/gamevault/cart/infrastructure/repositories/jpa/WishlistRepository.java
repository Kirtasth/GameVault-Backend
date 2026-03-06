package com.kirtasth.gamevault.cart.infrastructure.repositories.jpa;

import com.kirtasth.gamevault.cart.infrastructure.dtos.entities.WishlistEntity;
import com.kirtasth.gamevault.cart.infrastructure.dtos.entities.WishlistKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistEntity, WishlistKey> {
    List<WishlistEntity> findByUserId(Long userId);
}
