package com.kirtasth.gamevault.users.domain.ports.in;

import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.AccessJwt;
import com.kirtasth.gamevault.users.domain.models.RefreshToken;
import com.kirtasth.gamevault.users.domain.models.Role;

import java.util.List;

public interface JwtServicePort {

    Result<AccessJwt> getAccessJwt(Long userId, String email, List<Role> roles);

    Result<RefreshToken> isNotExpiredAndNotRevoked(String refreshToken);

    Result<Void> revokeAll(Long userId);

    Result<String> extractEmail(String token);

    Result<Void> isTokenValid(String token);
}
