package com.kirtasth.gamevault.checkout.infrastructure.controllers;

import com.kirtasth.gamevault.checkout.domain.ports.in.CheckoutUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/checkout/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final CheckoutUseCase checkoutUseCase;

    @PostMapping
    public ResponseEntity<Void> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        checkoutUseCase.handleStripeEvent(payload, sigHeader);
        return ResponseEntity.ok().build();
    }
}
