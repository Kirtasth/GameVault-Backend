package com.kirtasth.gamevault.auth.infrastructure.repositories;

import com.kirtasth.gamevault.auth.infrastructure.dtos.entities.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);


}
