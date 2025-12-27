package com.kirtasth.gamevault.auth.domain.ports.in;

import com.kirtasth.gamevault.auth.domain.models.AccessJwt;
import com.kirtasth.gamevault.auth.domain.models.AuthUser;
import com.kirtasth.gamevault.common.models.util.Result;

public interface JwtServicePort {

    Result<AccessJwt> getAccessJwt(AuthUser authUser);
    Result<String> extractEmail(String token);
    boolean isTokenNotExpiredAndValid(String token, AuthUser authUser);
}
