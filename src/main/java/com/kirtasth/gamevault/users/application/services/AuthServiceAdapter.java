package com.kirtasth.gamevault.users.application.services;

import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.*;
import com.kirtasth.gamevault.users.domain.ports.in.AuthServicePort;
import com.kirtasth.gamevault.users.domain.ports.in.JwtServicePort;
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

    @Override
    public Result<Void> registerUser(NewUser newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        var savedUser = userService.saveUser(newUser);

        if (savedUser instanceof Result.Failure<User>(
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

        var revokeResult = this.jwtService.revokeAll(authUser.getId());
        if (revokeResult instanceof Result.Failure<Void>(
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }

        return jwtService.getAccessJwt(authUser.getId(), authUser.getEmail(), authUser.getRoles());
    }

    @Override
    public Result<AccessJwt> refresh(RefreshTokenPetition refreshTokenPetition) {
        var refreshToken = refreshTokenPetition.getRefreshToken();
        var refreshTokenResult = jwtService.isNotExpiredAndNotRevoked(refreshToken);

        if (refreshTokenResult instanceof Result.Failure<RefreshToken>(
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }
        var userId = ((Result.Success<RefreshToken>) refreshTokenResult).data().getUserId();
        var revokeAllResult = jwtService.revokeAll(userId);

        if (revokeAllResult instanceof Result.Failure<Void>(
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }

        var userResult = this.userService.getUserById(userId);

        if (userResult instanceof Result.Failure<User>(
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }

        var user = ((Result.Success<User>) userResult).data();

        return this.jwtService.getAccessJwt(userId, user.getEmail(), user.getRoles());
    }

    @Override
    public Result<Void> logout(Long userId) {
        return jwtService.revokeAll(userId);
    }
}
