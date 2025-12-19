package com.kirtasth.gamevault.auth.domain.ports.in;

import com.kirtasth.gamevault.auth.domain.models.AuthUser;
import io.jsonwebtoken.Claims;

import java.util.function.Function;

public interface JwtServicePort {

    String extractEmail(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    String generateAccessToken(AuthUser authUser);
    String generateRefreshToken(AuthUser authUser);
    boolean isTokenValid(String token, AuthUser authUser);
}
