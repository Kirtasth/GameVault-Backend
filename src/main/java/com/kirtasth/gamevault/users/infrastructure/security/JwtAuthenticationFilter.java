package com.kirtasth.gamevault.users.infrastructure.security;

import com.kirtasth.gamevault.users.domain.models.AuthUser;
import com.kirtasth.gamevault.users.domain.ports.in.JwtServicePort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtServicePort jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid JWT token in Authorization header");
            return;
        }

        jwt = authHeader.substring(7);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            if (this.jwtService.isTokenValid(jwt)) {
                var userEmail = this.jwtService.extractEmail(jwt);
                var authUser = (AuthUser) userDetailsService.loadUserByUsername(userEmail);
                var authToken = new UsernamePasswordAuthenticationToken(
                        authUser,
                        null,
                        authUser.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}