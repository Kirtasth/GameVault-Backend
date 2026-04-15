package com.kirtasth.gamevault.users.infrastructure.controllers;


import com.kirtasth.gamevault.users.domain.models.AuthUser;
import com.kirtasth.gamevault.users.domain.ports.in.AuthServicePort;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.CredentialsRequest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.NewUserRequest;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.RefreshTokenPetitionRequest;
import com.kirtasth.gamevault.users.infrastructure.mappers.AuthMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        authService.registerUser(authMapper.toNewUser(newUserRequest));

        return ResponseEntity.status(HttpStatus.CREATED).contentLength(0).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid CredentialsRequest credentialsRequest
    ) {
        var credentials = this.authMapper.toCredentials(credentialsRequest);
        var accessJwt = this.authService.login(credentials);
        return ResponseEntity.ok(this.authMapper.toAccessJwtResponse(accessJwt));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestBody @Valid RefreshTokenPetitionRequest refreshTokenPetitionRequest
    ) {
        var accessJwt = this.authService.refresh(this.authMapper.toRefreshTokenPetition(refreshTokenPetitionRequest));

        return ResponseEntity.ok(this.authMapper.toAccessJwtResponse(accessJwt));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @AuthenticationPrincipal AuthUser user
    ) {
        this.authService.logout(user.getId());
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
