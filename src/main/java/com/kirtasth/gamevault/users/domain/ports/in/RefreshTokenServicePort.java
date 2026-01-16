package com.kirtasth.gamevault.users.domain.ports.in;

import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.RefreshToken;
import com.kirtasth.gamevault.users.domain.models.RefreshTokenPetition;

public interface RefreshTokenServicePort {

    Result<RefreshToken> save(RefreshToken refreshToken);
    Result<RefreshToken> refresh(RefreshTokenPetition refreshTokenPetition);
    Result<Void> revokeByUserId(Long userId);
    Result<Void> validate(String token);
    Result<String> extractEmail(String token);
}
