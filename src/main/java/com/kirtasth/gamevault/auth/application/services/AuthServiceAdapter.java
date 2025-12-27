package com.kirtasth.gamevault.auth.application.services;

import com.kirtasth.gamevault.auth.domain.models.AccessJwt;
import com.kirtasth.gamevault.auth.domain.models.AuthUser;
import com.kirtasth.gamevault.auth.domain.models.Credentials;
import com.kirtasth.gamevault.auth.domain.models.NewUser;
import com.kirtasth.gamevault.auth.domain.ports.in.AuthServicePort;
import com.kirtasth.gamevault.auth.domain.ports.in.JwtServicePort;
import com.kirtasth.gamevault.auth.domain.ports.out.AuthProviderPort;
import com.kirtasth.gamevault.auth.domain.ports.out.UsersProviderPort;
import com.kirtasth.gamevault.common.models.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceAdapter implements AuthServicePort {

    private final AuthProviderPort authProviderPort;
    private final PasswordEncoder passwordEncoder;
    private final UsersProviderPort usersProvider;
    private final JwtServicePort jwtService;

    @Override
    public Result<Void> registerUser(NewUser newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return usersProvider.createUser(newUser);
    }


    @Override
    public Result<AccessJwt> login(Credentials credentials) {
        var email = credentials.getEmail();
        var password = credentials.getPassword();

        var authentication = new UsernamePasswordAuthenticationToken(email, password, List.of());
        var authenticationAuthenticatedResult = authProviderPort.authenticate(authentication);

        if (authenticationAuthenticatedResult instanceof Result.Success<Authentication>(Authentication auth)) {
            var authUser = (AuthUser) auth.getPrincipal();

            return this.jwtService.getAccessJwt(authUser);
        }
        var failure = (Result.Failure<Authentication>) authenticationAuthenticatedResult;

        return new Result.Failure<>(
                failure.errorCode(),
                failure.errorMsg(),
                failure.errorDetails(),
                failure.exception()
        );
    }

}
