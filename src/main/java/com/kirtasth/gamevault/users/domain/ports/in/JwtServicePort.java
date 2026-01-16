package com.kirtasth.gamevault.users.domain.ports.in;

import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.AccessJwt;
import com.kirtasth.gamevault.users.domain.models.AuthUser;
import com.kirtasth.gamevault.users.domain.models.RefreshToken;

public interface JwtServicePort {

    Result<AccessJwt> getAccessJwt(AuthUser authUser);

    Result<String> extractEmail(String token);

    Result<Void> verify(String token);

    Result<RefreshToken> generateRefresh(String token);
}
