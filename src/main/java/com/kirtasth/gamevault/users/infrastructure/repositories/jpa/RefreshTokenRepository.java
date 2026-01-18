package com.kirtasth.gamevault.users.infrastructure.repositories.jpa;

import com.kirtasth.gamevault.users.infrastructure.dtos.entities.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    Optional<RefreshTokenEntity> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
