package com.kirtasth.gamevault.auth.domain.ports.out;

import com.kirtasth.gamevault.auth.domain.models.RefreshToken;
import com.kirtasth.gamevault.common.models.util.Result;

public interface JwtRepoPort {

    Result<RefreshToken> findByToken(String token);
    Result<RefreshToken> save(RefreshToken refreshToken);

}
