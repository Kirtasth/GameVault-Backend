package com.kirtasth.gamevault.checkout.infrastructure.controllers;

import com.kirtasth.gamevault.checkout.domain.ports.in.CheckoutUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/checkout/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final CheckoutUseCase checkoutUseCase;

    @PostMapping
    public ResponseEntity<Void> handleStripeWebhook(
            @RequestBody byte[] payload,
            @RequestHeader Map<String, String> headers
    ) {
        String sigHeader = headers.get("stripe-signature");
        if (sigHeader == null) {
            sigHeader = headers.get("Stripe-Signature");
        }

        log.info("Received Stripe webhook. All headers: {}", headers);
        
        if (sigHeader == null) {
            log.error("Stripe-Signature header is MISSING! Available headers: {}", headers.keySet());
            return ResponseEntity.badRequest().build();
        }
        
        checkoutUseCase.handleStripeWebhook(new String(payload, StandardCharsets.UTF_8), sigHeader);
        return ResponseEntity.ok().build();
    }
}
