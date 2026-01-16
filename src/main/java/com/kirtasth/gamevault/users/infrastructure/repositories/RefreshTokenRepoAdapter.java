package com.kirtasth.gamevault.users.infrastructure.repositories;

import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.RefreshToken;
import com.kirtasth.gamevault.users.domain.ports.out.RefreshTokenRepoPort;
import com.kirtasth.gamevault.users.infrastructure.mappers.AuthMapper;
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
    public Result<RefreshToken> save(RefreshToken refreshToken) {
        var savedRefreshToken = this.refreshTokenRepository.save(this.authMapper.toRefreshTokenEntity(refreshToken));

        return new Result.Success<>(this.authMapper.toRefreshToken(savedRefreshToken));
    }

    @Override
    public Result<Void> revoke(String token) {
        var refreshToken = this.refreshTokenRepository.findByToken(token);

        if (refreshToken.isEmpty()) {
            return new Result.Failure<>(
                    400,
                    "Could not revoke token",
                    Map.of("details", "Token not found"),
                    null
            );
        }
        refreshToken.get().setRevokedAt(Instant.now());
        this.refreshTokenRepository.save(refreshToken.get());

        return new Result.Success<>(null);
    }

    @Override
    public Result<Void> revokeByUserId(Long userId) {
        var refreshToken = this.refreshTokenRepository.findByUserId(userId);
        if (refreshToken.isEmpty()) {
            return new Result.Failure<>(
                    404,
                    "Could not revoke token",
                    Map.of("details", "Token not found"),
                    null
            );
        }

        refreshToken.get().setRevokedAt(Instant.now());
        this.refreshTokenRepository.save(refreshToken.get());

        return new Result.Success<>(null);
    }
}
