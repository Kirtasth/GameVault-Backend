package com.kirtasth.gamevault.users.application.services;

import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.*;
import com.kirtasth.gamevault.users.domain.ports.in.AuthServicePort;
import com.kirtasth.gamevault.users.domain.ports.in.JwtServicePort;
import com.kirtasth.gamevault.users.domain.ports.in.RefreshTokenServicePort;
import com.kirtasth.gamevault.users.domain.ports.in.UserServicePort;
import com.kirtasth.gamevault.users.domain.ports.out.AuthProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceAdapter implements AuthServicePort {

    private final AuthProviderPort authProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserServicePort userService;
    private final JwtServicePort jwtService;
    private final RefreshTokenServicePort refreshTokenService;

    @Override
    public Result<Void> registerUser(NewUser newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        var savedUser = userService.saveUser(newUser);

        if (savedUser instanceof Result.Failure<User> (
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }

        return new Result.Success<>(null);
    }


    @Override
    public Result<AccessJwt> login(Credentials credentials) {
        var email = credentials.getEmail();
        var password = credentials.getPassword();

        var authentication = new UsernamePasswordAuthenticationToken(email, password, List.of());
        var authenticationAuthenticatedResult = authProvider.authenticate(authentication);

        if (authenticationAuthenticatedResult instanceof Result.Failure<Authentication>(
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }

        var auth = ((Result.Success<Authentication>) authenticationAuthenticatedResult).data();

        var authUser = (AuthUser) auth.getPrincipal();

        var jwtResult = jwtService.getAccessJwt(authUser);

        if (jwtResult instanceof Result.Failure<AccessJwt>(
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }

        var refreshToken = new RefreshToken(
                null,
                authUser.getId(),
                ((Result.Success<AccessJwt>) jwtResult).data().getRefreshToken(),
                null,
                null
        );

        var refreshTokenResult = this.refreshTokenService.save(refreshToken);

        if (refreshTokenResult instanceof Result.Failure<RefreshToken> (
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }

        return jwtResult;
    }

    @Override
    public Result<RefreshToken> refresh(RefreshTokenPetition refreshTokenPetition) {
        return this.refreshTokenService.refresh(refreshTokenPetition);
    }

    @Override
    public Result<Void> logout(Long userId) {
        return this.refreshTokenService.revokeByUserId(userId);
    }
}
