package com.kirtasth.gamevault.checkout.infrastructure.controllers;

import com.kirtasth.gamevault.checkout.domain.ports.in.CheckoutUseCase;
import com.kirtasth.gamevault.users.domain.models.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutUseCase checkoutUseCase;

    @PostMapping
    public ResponseEntity<Map<String, String>> createCheckoutSession(Authentication authentication) {
        var userId = ((AuthUser) authentication.getPrincipal()).getId();
        String url = checkoutUseCase.createCheckoutSession(userId);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
