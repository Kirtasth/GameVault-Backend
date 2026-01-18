package com.kirtasth.gamevault.users.domain.ports.out;

import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.RefreshToken;

public interface RefreshTokenRepoPort {

    Result<RefreshToken> findByToken(String token);
    Result<RefreshToken> save(RefreshToken refreshToken);
    Result<Void> revoke(String token);
    Result<Void> revokeByUserId(Long userId);
}
