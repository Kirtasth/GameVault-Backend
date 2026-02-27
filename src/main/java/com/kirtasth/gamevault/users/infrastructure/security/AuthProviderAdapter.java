package com.kirtasth.gamevault.users.infrastructure.security;

import com.kirtasth.gamevault.users.domain.ports.out.AuthProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthProviderAdapter implements AuthProviderPort {

    private final AuthenticationProvider authenticationProvider;

    @Override
    public Authentication authenticate(Authentication authentication) {

        return this.authenticationProvider.authenticate(authentication);
    }
}
