package com.kirtasth.gamevault.users.infrastructure.repositories;

import com.kirtasth.gamevault.users.application.exception.TokenInvalidException;
import com.kirtasth.gamevault.users.domain.models.RefreshToken;
import com.kirtasth.gamevault.users.domain.ports.out.RefreshTokenRepoPort;
import com.kirtasth.gamevault.users.infrastructure.mappers.AuthMapper;
import com.kirtasth.gamevault.users.infrastructure.repositories.jpa.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepoAdapter implements RefreshTokenRepoPort {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthMapper authMapper;

    @Override
    public RefreshToken findByToken(String token) throws TokenInvalidException {
        var refreshToken = this.refreshTokenRepository.findByToken(token);
        if (refreshToken.isEmpty()) {
            throw new TokenInvalidException("Token not found in the database");
        }

        return this.authMapper.toRefreshToken(refreshToken.get());
    }

    @Override
    public void revokeAllByUserId(Long userId) throws TokenInvalidException {
        try {
            this.refreshTokenRepository.findAllByUserIdAndRevokedAtIsNull(userId).forEach(
                    refreshTokenEntity -> {
                        refreshTokenEntity.setRevokedAt(Instant.now());
                        this.refreshTokenRepository.save(refreshTokenEntity);
                    }
            );
        } catch (DataIntegrityViolationException e) {
            throw new TokenInvalidException("Unable to revoke tokens for user with id: " + userId + ".");
        }
    }

    @Override
    public void save(RefreshToken refreshToken) throws TokenInvalidException {
        try {
            this.refreshTokenRepository.save(this.authMapper.toRefreshTokenEntity(refreshToken));
        } catch (DataIntegrityViolationException e) {
            throw new TokenInvalidException("Unable to save token for user with id: " + refreshToken.getUserId() + ".");
        }
    }
}
