package com.kirtasth.gamevault.users.infrastructure.config;

import com.kirtasth.gamevault.common.domain.models.enums.RoleEnum;
import com.kirtasth.gamevault.users.infrastructure.security.JwtAuthenticationFilter;
import com.kirtasth.gamevault.users.infrastructure.security.PermissionChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final PermissionChecker permissionChecker;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsSpec -> corsSpec.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of("*"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))

                .exceptionHandling(exception -> exception.
                        authenticationEntryPoint(authenticationEntryPoint))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").access(
                                permissionChecker.isAuthenticated())
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{userId}").access(
                                permissionChecker.isOwnerOrAdmin("userId"))
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/{userId}").access(
                                permissionChecker.isOwnerOrAdmin("userId"))
                        .requestMatchers(HttpMethod.GET, "/api/v1/users").access(
                                permissionChecker.isAdmin())
                        .requestMatchers(HttpMethod.POST, "/api/v1/catalog/developer").access(
                                permissionChecker.isAuthenticated())
                        .requestMatchers(HttpMethod.POST, "/api/v1/catalog").access(
                                permissionChecker.hasRole(RoleEnum.DEVELOPER))
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalog/my-games").access(
                                permissionChecker.hasRole(RoleEnum.DEVELOPER))
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalog/purchased-games").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalog/custom-game-list").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalog").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/cart").access(
                                permissionChecker.isAuthenticated())
                        .requestMatchers(HttpMethod.POST, "/api/v1/cart").access(
                                permissionChecker.isAuthenticated())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cart/items/{itemId}").access(
                                permissionChecker.isAuthenticated())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cart").access(
                                permissionChecker.isAuthenticated())
                        .requestMatchers(HttpMethod.GET, "/api/v1/wishlist").access(
                                permissionChecker.isAuthenticated())
                        .requestMatchers(HttpMethod.POST, "/api/v1/wishlist/{gameId}").access(
                                permissionChecker.isAuthenticated())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/wishlist/{gameId}").access(
                                permissionChecker.isAuthenticated())


                        .anyRequest().permitAll())

                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
