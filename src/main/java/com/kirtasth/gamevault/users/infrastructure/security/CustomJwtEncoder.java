package com.kirtasth.gamevault.users.infrastructure.security;

import com.kirtasth.gamevault.users.domain.ports.in.KeyGeneratorPort;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
@RequiredArgsConstructor
public class CustomJwtEncoder implements JwtEncoder {

    private final KeyGeneratorPort keyGenerator;

    @Override
    public Jwt encode(JwtEncoderParameters parameters) throws JwtEncodingException {
        RSAPublicKey publicKey = (RSAPublicKey) keyGenerator.getPublicKey();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyGenerator.getPrivateKey();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        ImmutableJWKSet<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);

        var jwkEncoder = new NimbusJwtEncoder(jwkSource);
        return jwkEncoder.encode(parameters);
    }
}
