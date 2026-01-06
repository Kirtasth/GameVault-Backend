package com.kirtasth.gamevault.auth.infrastructure.repositories;

import com.kirtasth.gamevault.auth.domain.models.RefreshToken;
import com.kirtasth.gamevault.auth.domain.ports.out.JwtRepoPort;
import com.kirtasth.gamevault.auth.infrastructure.mappers.AuthMapper;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.ports.out.UserRepoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JwtRepoAdapter implements JwtRepoPort {

    private final JwtRepository jwtRepository;
    private final UserRepoPort userRepoPort;
    private final AuthMapper authMapper;

    @Override
    public Result<RefreshToken> findByToken(String token) {
        var refreshToken = this.jwtRepository.findByToken(token);
        if (refreshToken.isEmpty()) {
            return new Result.Failure<>(
                    404,
                    "Refresh token not found",
                    null,
                    null
            );
        }

        return new Result.Success<>(this.authMapper.toRefreshToken(refreshToken.get()));
    }

    @Override
    public Result<RefreshToken> save(RefreshToken refreshToken) {
        var userReference = this.userRepoPort.getReference(refreshToken.getUserId());
        var savedRefreshToken = this.jwtRepository.save(this.authMapper.toRefreshTokenEntity(refreshToken, userReference));

        return new Result.Success<>(this.authMapper.toRefreshToken(savedRefreshToken));
    }
}
