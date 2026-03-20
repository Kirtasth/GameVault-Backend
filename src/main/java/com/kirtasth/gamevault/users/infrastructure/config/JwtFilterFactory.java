package com.kirtasth.gamevault.users.infrastructure.config;

import com.kirtasth.gamevault.users.domain.ports.in.JwtServicePort;
import com.kirtasth.gamevault.users.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilterFactory {

    private final UserDetailsService userDetailsService;
    private final JwtServicePort jwtService;

    public OncePerRequestFilter jwtFilter() {
        return new JwtAuthenticationFilter(this.userDetailsService, this.jwtService);
    }
}
