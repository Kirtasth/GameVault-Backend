package com.kirtasth.gamevault.users.domain.ports.in;

import com.kirtasth.gamevault.users.application.exception.TokenInvalidException;
import com.kirtasth.gamevault.users.domain.models.AccessJwt;
import com.kirtasth.gamevault.users.domain.models.RefreshToken;
import com.kirtasth.gamevault.users.domain.models.Role;

import java.util.List;

public interface JwtServicePort {

    AccessJwt getAccessJwt(Long userId, String email, List<Role> roles);

    RefreshToken isNotExpiredAndNotRevoked(String refreshToken);

    void revokeAll(Long userId);

    String extractEmail(String token) throws TokenInvalidException;

    boolean isTokenValid(String token);
}
