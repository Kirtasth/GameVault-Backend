package com.kirtasth.gamevault.users.infrastructure.security;

import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.application.jwt.JwtKeyGenerator;
import com.kirtasth.gamevault.users.domain.models.AccessJwt;
import com.kirtasth.gamevault.users.domain.models.RefreshToken;
import com.kirtasth.gamevault.users.domain.models.Role;
import com.kirtasth.gamevault.users.domain.ports.in.JwtServicePort;
import com.kirtasth.gamevault.users.domain.ports.out.RefreshTokenRepoPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private final RefreshTokenRepoPort refreshTokenRepository;

    @Override
    public Result<AccessJwt> getAccessJwt(Long userId, String email, List<Role> roles) {
        try {
            var accessToken = generateAccessToken(email, roles);
            var refreshTokenStr = generateRefreshToken(email, roles);

            var refreshToken = new RefreshToken();
            refreshToken.setUserId(userId);
            refreshToken.setToken(refreshTokenStr);

            this.refreshTokenRepository.save(refreshToken);

            var accessJwt = new AccessJwt(
                    userId,
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
    public Result<RefreshToken> isNotExpiredAndNotRevoked(String refreshToken) {
        try {
            var claims = getTokenClaims(refreshToken);
            var isExpired = isTokenExpired(claims);

            if (isExpired) {
                return new Result.Failure<>(401, "Token expired", null, null);
            }
            var storedTokenResult = this.refreshTokenRepository.findByToken(refreshToken);
            if (storedTokenResult instanceof Result.Failure<RefreshToken>(
                    int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
            )) {
                return new Result.Failure<>(errorCode, errorMsg, errorDetails, exception);
            }
            var storedToken = ((Result.Success<RefreshToken>) storedTokenResult).data();
            if (storedToken.getRevokedAt() != null) {
                return new Result.Failure<>(401, "Token revoked", null, null);
            }
            return new Result.Success<>(storedToken);
        } catch (JwtException e) {
            return new Result.Failure<>(401, "Invalid token", Map.of("details", e.getMessage()), e);
        }
    }

    @Override
    public Result<Void> revokeAll(Long userId) {
        this.refreshTokenRepository.revokeAllByUserId(userId);
        return new Result.Success<>(null);
    }

    @Override
    public Result<String> extractEmail(String token) {
        try {
            var claims = getTokenClaims(token);
            return new Result.Success<>(claims.getSubject());
        } catch (JwtException e) {
            return new Result.Failure<>(401, "Invalid token", Map.of("details", e.getMessage()), e);
        }
    }

    @Override
    public Result<Void> isTokenValid(String token) {
        try {
            var claims = getTokenClaims(token);
            var isExpired = isTokenExpired(claims);

            if (isExpired) {
                return new Result.Failure<>(401, "Token expired", null, null);
            }

            var email = claims.getSubject();

            if (email == null) {
                return new Result.Failure<>(401, "Invalid token", null, null);
            }

            return new Result.Success<>(null);
        } catch (JwtException e) {
            return new Result.Failure<>(401, "Invalid token", Map.of("details", e.getMessage()), e);
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

    private String generateAccessToken(String email, List<Role> userRoles) throws JwtException {
        Map<String, Object> claims = new HashMap<>();

        String roles = userRoles.stream()
                .map(role -> role.getRole().name())
                .collect(Collectors.joining(" "));
        claims.put("roles", roles);

        return buildToken(claims, email, jwtExpiration);
    }

    private String generateRefreshToken(String email, List<Role> userRoles) throws JwtException {
        Map<String, Object> claims = new HashMap<>();

        String roles = userRoles.stream()
                .map(role -> role.getRole().name())
                .collect(Collectors.joining(" "));
        claims.put("roles", roles);

        return buildToken(claims, email, jwtRefreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, String email, long expiration) throws JwtException {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(jwtKeyGenerator.getPrivateKey())
                .compact();
    }
}
