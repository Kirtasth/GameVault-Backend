package com.kirtasth.gamevault.users.infrastructure.repositories;

import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.RefreshToken;
import com.kirtasth.gamevault.users.domain.ports.out.RefreshTokenRepoPort;
import com.kirtasth.gamevault.users.infrastructure.mappers.AuthMapper;
import com.kirtasth.gamevault.users.infrastructure.repositories.jpa.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepoAdapter implements RefreshTokenRepoPort {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthMapper authMapper;

    @Override
    public Result<RefreshToken> findByToken(String token) {
        var refreshToken = this.refreshTokenRepository.findByToken(token);
        if (refreshToken.isEmpty()) {
            return new Result.Failure<>(
                    404,
                    "Refresh token not found",
                    Map.of("details", "Refresh token not found, try to login first and then refresh token"),
                    null
            );
        }

        return new Result.Success<>(this.authMapper.toRefreshToken(refreshToken.get()));
    }

    @Override
    public void revokeAllByUserId(Long userId) {
        this.refreshTokenRepository.findAllByUserIdAndRevokedAtIsNull(userId).forEach(
                refreshTokenEntity -> {
                    refreshTokenEntity.setRevokedAt(Instant.now());
                    this.refreshTokenRepository.save(refreshTokenEntity);
                }
        );
    }

    @Override
    public void save(RefreshToken refreshToken) {
        this.refreshTokenRepository.save(this.authMapper.toRefreshTokenEntity(refreshToken));
    }
}
