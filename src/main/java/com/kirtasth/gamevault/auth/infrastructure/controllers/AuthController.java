package com.kirtasth.gamevault.auth.infrastructure.controllers;


import com.kirtasth.gamevault.auth.domain.models.AccessJwt;
import com.kirtasth.gamevault.auth.domain.ports.in.AuthServicePort;
import com.kirtasth.gamevault.auth.infrastructure.dtos.requests.CredentialsRequest;
import com.kirtasth.gamevault.auth.infrastructure.dtos.requests.NewUserRequest;
import com.kirtasth.gamevault.auth.infrastructure.mappers.AuthMapper;
import com.kirtasth.gamevault.common.infrastructure.responses.ErrorResponse;
import com.kirtasth.gamevault.common.models.util.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthMapper authMapper;
    private final AuthServicePort authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid NewUserRequest newUserRequest
    ) {
        var newUser = authMapper.toNewUser(newUserRequest);
        var result = authService.registerUser(newUser);

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
}
