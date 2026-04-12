package com.kirtasth.gamevault.checkout.infrastructure.controllers;

import com.kirtasth.gamevault.checkout.domain.ports.in.ManageGameKeysUseCase;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.requests.UploadGameKeysRequest;
import com.kirtasth.gamevault.checkout.infrastructure.dtos.responses.GameKeyResponse;
import com.kirtasth.gamevault.checkout.infrastructure.mappers.GameKeyMapper;
import com.kirtasth.gamevault.users.domain.models.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class GameKeyController {

    private final ManageGameKeysUseCase manageGameKeysUseCase;
    private final GameKeyMapper gameKeyMapper;

    @PostMapping("/games/{gameId}/keys")
    public ResponseEntity<Void> uploadKeys(
            @PathVariable Long gameId,
            @RequestBody @Valid UploadGameKeysRequest request,
            Authentication authentication
    ) {
        Long developerId = ((AuthUser) authentication.getPrincipal()).getId();
        manageGameKeysUseCase.uploadKeys(developerId, gameId, request.getKeys());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/games/{gameId}/keys")
    public ResponseEntity<List<GameKeyResponse>> getKeys(
            @PathVariable Long gameId,
            Authentication authentication
    ) {
        Long developerId = ((AuthUser) authentication.getPrincipal()).getId();
        List<GameKeyResponse> responses = manageGameKeysUseCase.getKeysByGameId(developerId, gameId).stream()
                .map(gameKeyMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/game-keys/{keyId}/mark-as-used")
    public ResponseEntity<Void> markKeyAsUsed(
            @PathVariable Long keyId,
            Authentication authentication
    ) {
        Long developerId = ((AuthUser) authentication.getPrincipal()).getId();
        manageGameKeysUseCase.markKeyAsUsed(developerId, keyId);
        return ResponseEntity.noContent().build();
    }
}
