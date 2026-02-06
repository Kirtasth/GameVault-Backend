package com.kirtasth.gamevault.users.domain.ports.out;

import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.RefreshToken;

public interface RefreshTokenRepoPort {

    Result<RefreshToken> findByToken(String token);

    void revokeAllByUserId(Long userId);

    void save(RefreshToken refreshToken);
}
