package com.kirtasth.gamevault.checkout.domain.ports.in;

public interface CheckoutUseCase {
    String createCheckoutSession(Long userId);
    void handleStripeEvent(String payload, String sigHeader);
}
