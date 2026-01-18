package com.kirtasth.gamevault.users.application.services;

import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.RefreshToken;
import com.kirtasth.gamevault.users.domain.models.RefreshTokenPetition;
import com.kirtasth.gamevault.users.domain.ports.in.JwtServicePort;
import com.kirtasth.gamevault.users.domain.ports.in.RefreshTokenServicePort;
import com.kirtasth.gamevault.users.domain.ports.out.RefreshTokenRepoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceAdapter implements RefreshTokenServicePort {

    private final RefreshTokenRepoPort refreshTokenRepo;
    private final JwtServicePort jwtService;

    @Override
    public Result<RefreshToken> save(RefreshToken refreshToken) {
        return this.refreshTokenRepo.save(refreshToken);
    }

    @Override
    public Result<RefreshToken> refresh(RefreshTokenPetition refreshTokenPetition) {
        var petitionToken = refreshTokenPetition.getRefreshToken();

        var verification = this.jwtService.verify(petitionToken);
        if (verification instanceof Result.Failure<Void> (
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }

        var refreshTokenResult = this.refreshTokenRepo.findByToken(petitionToken);

        if (refreshTokenResult instanceof Result.Failure<RefreshToken>(
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }
        var refreshToken = ((Result.Success<RefreshToken>) refreshTokenResult).data();

        if (refreshToken.getRevokedAt() != null) {
            return new Result.Failure<>(
                    401,
                    "Token has been revoked before, try to login again",
                    null,
                    null
            );
        }
        var revokeTokenResult = this.refreshTokenRepo.revoke(refreshToken.getToken());

        if (revokeTokenResult instanceof Result.Failure<Void>(
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }

        var newTokenResult = this.jwtService.generateRefresh(refreshToken.getToken());

        if (newTokenResult instanceof Result.Failure<RefreshToken>(
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }

        var newToken = ((Result.Success<RefreshToken>) newTokenResult).data();
        newToken.setUserId(refreshToken.getUserId());

        return this.refreshTokenRepo.save(newToken);
    }

    @Override
    public Result<Void> revokeByUserId(Long userId) {
        return this.refreshTokenRepo.revokeByUserId(userId);
    }

    @Override
    public Result<Void> validate(String token) {
        var verification = this.jwtService.verify(token);

        if (verification instanceof Result.Failure<Void>(
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }

        var refreshTokenResult = this.refreshTokenRepo.findByToken(token);

        if (refreshTokenResult instanceof Result.Failure<RefreshToken> (
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )){
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }

        var refreshToken = ((Result.Success<RefreshToken>) refreshTokenResult).data();

        if (refreshToken.getRevokedAt() != null) {
            this.refreshTokenRepo.revokeByUserId(refreshToken.getUserId());

            return new Result.Failure<>(
                    401,
                    "Token has been revoked before, try to login again",
                    null,
                    null
            );
        }

        return new Result.Success<>(null);
    }

    @Override
    public Result<String> extractEmail(String token) {
        return this.jwtService.extractEmail(token);
    }
}
