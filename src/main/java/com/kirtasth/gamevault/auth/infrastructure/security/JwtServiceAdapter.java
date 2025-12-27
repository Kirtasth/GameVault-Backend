package com.kirtasth.gamevault.auth.infrastructure.security;

import com.kirtasth.gamevault.auth.application.jwt.JwtKeyGenerator;
import com.kirtasth.gamevault.auth.domain.models.AccessJwt;
import com.kirtasth.gamevault.auth.domain.models.AuthUser;
import com.kirtasth.gamevault.auth.domain.ports.in.JwtServicePort;
import com.kirtasth.gamevault.common.models.util.Result;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtServiceAdapter implements JwtServicePort {

    @Getter
    @Value("${spring.application.security.jwt.expiration}")
    private long jwtExpiration;

    @Getter
    @Value("${spring.application.security.jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    private final JwtKeyGenerator jwtKeyGenerator;

    @Override
    public Result<AccessJwt> getAccessJwt(AuthUser authUser) {
        try {
            var accessToken = generateAccessToken(authUser);
            var refreshToken = generateRefreshToken(authUser);

            var accessJwt = new AccessJwt(
                    authUser.getId(),
                    accessToken,
                    refreshToken,
                    "Bearer",
                    jwtExpiration,
                    jwtRefreshExpiration
            );

            return new Result.Success<>(accessJwt);
        } catch (JwtException e) {
            return new Result.Failure<>(
                    401,
                    e.getMessage(),
                    null,
                    e
            );
        }
    }

    @Override
    public Result<String> extractEmail(String token) {
        try {
            var claims = getTokenClaims(token);

            return new Result.Success<>(claims.getSubject());
        } catch (JwtException e) {
            return new Result.Failure<>(
                    401,
                    e.getMessage(),
                    null,
                    e
            );
        }
    }

    @Override
    public boolean isTokenNotExpiredAndValid(String token, AuthUser authUser) {
        try {
            var claims = getTokenClaims(token);

            var isExpired = isTokenExpired(claims);
            var isValid = isTokenValid(claims, authUser);

            return !isExpired && isValid;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims getTokenClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith((PublicKey) jwtKeyGenerator.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date(System.currentTimeMillis()));
    }

    private boolean isTokenValid(Claims claims, AuthUser authUser) throws JwtException {
        var email = claims.getSubject();
        return email != null && email.equals(authUser.getEmail());
    }

    private String generateAccessToken(AuthUser authUser) throws JwtException {
        Map<String, Object> claims = new HashMap<>();

        String roles = authUser.getRoles().stream()
                .map(role -> role.getRole().name())
                .collect(Collectors.joining(" "));
        claims.put("roles", roles);

        return buildToken(claims, authUser, jwtExpiration);
    }

    private String generateRefreshToken(AuthUser authUser) throws JwtException {
        return buildToken(new HashMap<>(), authUser, jwtRefreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, AuthUser authUser, long expiration) throws JwtException {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(authUser.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(jwtKeyGenerator.getPrivateKey())
                .compact();
    }
}
