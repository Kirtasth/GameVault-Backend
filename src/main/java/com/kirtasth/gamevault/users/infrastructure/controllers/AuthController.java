package com.kirtasth.gamevault.users.infrastructure.controllers;


import com.kirtasth.gamevault.common.infrastructure.responses.ErrorResponse;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.AccessJwt;
import com.kirtasth.gamevault.users.domain.ports.in.AuthServicePort;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.CredentialsRequest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.NewUserRequest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.RefreshTokenPetitionRequest;
import com.kirtasth.gamevault.users.infrastructure.mappers.AuthMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthMapper authMapper;
    private final AuthServicePort authService;


    @GetMapping
    public ResponseEntity<String> validateToken() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid NewUserRequest newUserRequest
    ) {
        var result = authService.registerUser(authMapper.toNewUser(newUserRequest));

        if (result instanceof Result.Success<Void>) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        var failure = (Result.Failure<Void>) result;

        log.error(failure.errorMsg());

        return new ResponseEntity<>(
                new ErrorResponse(
                        failure.errorCode(),
                        failure.exception() == null
                                ? "UNKNOWN_EXCEPTION"
                                : failure.exception().getClass().getSimpleName(),
                        failure.errorMsg(),
                        failure.errorDetails()
                ),
                HttpStatusCode.valueOf(failure.errorCode())
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid CredentialsRequest credentialsRequest
    ) {
        var credentials = this.authMapper.toCredentials(credentialsRequest);
        var result = this.authService.login(credentials);

        if (result instanceof Result.Success<AccessJwt>(AccessJwt accessJwt)) {

            return ResponseEntity.ok(this.authMapper.toAccessJwtResponse(accessJwt));
        }

        var failure = (Result.Failure<AccessJwt>) result;
        log.error(failure.errorMsg());

        return new ResponseEntity<>(
                new ErrorResponse(
                        failure.errorCode(),
                        failure.exception() == null
                                ? "UNKNOWN_EXCEPTION"
                                : failure.exception().getClass().getSimpleName(),
                        failure.errorMsg(),
                        failure.errorDetails()
                ),
                HttpStatusCode.valueOf(failure.errorCode())
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestBody @Valid RefreshTokenPetitionRequest refreshTokenPetitionRequest
    ) {
        var refreshResult = this.authService.refresh(this.authMapper.toRefreshTokenPetition(refreshTokenPetitionRequest));

        if (refreshResult instanceof Result.Failure (
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new ResponseEntity<>(
                    new ErrorResponse(
                            errorCode,
                            exception == null
                                    ? "UNKNOWN_EXCEPTION"
                                    : exception.getClass().getSimpleName(),
                            errorMsg,
                            errorDetails
                    ),
                    HttpStatusCode.valueOf(errorCode)
            );
        }

        var refresh = ((Result.Success<AccessJwt>) refreshResult).data();

        return ResponseEntity.ok(this.authMapper.toAccessJwtResponse(refresh));
    }

    @PostMapping("/{userId}/logout")
    public ResponseEntity<?> logout(
            @PathVariable @NotNull @Min(1) Long userId
    ) {
        var logoutResult = this.authService.logout(userId);

        if (logoutResult instanceof Result.Failure<Void>(
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new ResponseEntity<>(
                    new ErrorResponse(
                            errorCode,
                            exception == null
                                    ? "UNKNOWN_EXCEPTION"
                                    : exception.getClass().getSimpleName(),
                            errorMsg,
                            errorDetails
                    ),
                    HttpStatusCode.valueOf(errorCode)
            );
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }
}
