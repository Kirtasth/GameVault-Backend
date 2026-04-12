package com.kirtasth.gamevault.checkout.domain.ports.in;

public interface CheckoutUseCase {
    String createCheckoutSession(Long userId);
    void handleStripeWebhook(String payload, String sigHeader);
}
