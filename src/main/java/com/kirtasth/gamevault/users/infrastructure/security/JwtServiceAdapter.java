package com.kirtasth.gamevault.users.infrastructure.security;

import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.application.jwt.JwtKeyGenerator;
import com.kirtasth.gamevault.users.domain.models.AccessJwt;
import com.kirtasth.gamevault.users.domain.models.AuthUser;
import com.kirtasth.gamevault.users.domain.models.RefreshToken;
import com.kirtasth.gamevault.users.domain.ports.in.JwtServicePort;
import com.kirtasth.gamevault.users.domain.ports.in.UserServicePort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
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

    @Value("${spring.application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${spring.application.security.jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    private final JwtKeyGenerator jwtKeyGenerator;

    @Override
    @Transactional
    public Result<AccessJwt> getAccessJwt(AuthUser authUser) {
        try {
            var accessToken = generateAccessToken(authUser);
            var refreshTokenStr = generateRefreshToken(authUser);

            var accessJwt = new AccessJwt(
                    authUser.getId(),
                    accessToken,
                    refreshTokenStr,
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
    public Result<Void> verify(String token) {
        try {
            var claims = getTokenClaims(token);

            var isExpired = isTokenExpired(claims);

            if (isExpired) {
                return new Result.Failure<>(
                        401,
                        "Token has expired",
                        null,
                        null
                );
            }
            return new Result.Success<>(null);
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
    public Result<RefreshToken> generateRefresh(String token) {
        try {
            var claims = getTokenClaims(token);

            var email = claims.getSubject();
            var roles = claims.get("roles", String.class);

            if (email == null || roles == null) {
                return new Result.Failure<>(
                        401,
                        "Invalid token",
                        null,
                        null
                );
            }

            var refreshJwt = Jwts.builder()
                    .claims(Map.of("roles", roles))
                    .subject(email)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpiration))
                    .signWith(jwtKeyGenerator.getPrivateKey())
                    .compact();

            var refreshToken = new RefreshToken(
                    null,
                    null,
                    refreshJwt,
                    new Date(System.currentTimeMillis()).toInstant(),
                    null

            );

            return new Result.Success<>(refreshToken);
        } catch (JwtException e) {
            return new Result.Failure<>(
                    401,
                    e.getMessage(),
                    null,
                    e
            );
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

    private String generateAccessToken(AuthUser authUser) throws JwtException {
        Map<String, Object> claims = new HashMap<>();

        String roles = authUser.getRoles().stream()
                .map(role -> role.getRole().name())
                .collect(Collectors.joining(" "));
        claims.put("roles", roles);

        return buildToken(claims, authUser, jwtExpiration);
    }

    private String generateRefreshToken(AuthUser authUser) throws JwtException {
        Map<String, Object> claims = new HashMap<>();

        String roles = authUser.getRoles().stream()
                .map(role -> role.getRole().name())
                .collect(Collectors.joining(" "));
        claims.put("roles", roles);

        return buildToken(claims, authUser, jwtRefreshExpiration);
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
