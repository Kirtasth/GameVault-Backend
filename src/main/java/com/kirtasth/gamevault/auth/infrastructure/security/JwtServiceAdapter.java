package com.kirtasth.gamevault.auth.infrastructure.security;

import com.kirtasth.gamevault.auth.application.jwt.JwtKeyGenerator;
import com.kirtasth.gamevault.auth.domain.models.AuthUser;
import com.kirtasth.gamevault.auth.domain.ports.in.JwtServicePort;
import com.kirtasth.gamevault.users.domain.models.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Maps;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtServiceAdapter implements JwtServicePort {

    @Value("${spring.application.security.jwt.expiration}:60")
    private long jwtExpiration;

    @Value("${spring.application.security.jwt.refresh-expiration}:3600")
    private long refreshExpiration;

    private final JwtKeyGenerator jwtKeyGenerator;

    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String generateAccessToken(AuthUser authUser) {
        Map<String, Object> claims = new HashMap<>();

        String roles = authUser.getRoles().stream()
                .map(role -> role.getRole().name())
                .collect(Collectors.joining(" "));
        claims.put("roles", roles);

        claims.put("uid", authUser.getId());

        return buildToken(claims, authUser, jwtExpiration);
    }

    @Override
    public String generateRefreshToken(AuthUser authUser) {
        return buildToken(new HashMap<>(), authUser, refreshExpiration);
    }

    @Override
    public boolean isTokenValid(String token, AuthUser authUser) {
        final String email = extractEmail(token);

        return email.equals(authUser.getEmail()) && !isTokenExpired(token);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) jwtKeyGenerator.getPrivateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String buildToken(Map<String, Object> extraClaims, AuthUser authUser, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(authUser.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(jwtKeyGenerator.getPrivateKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
