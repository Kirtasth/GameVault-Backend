package com.kirtasth.gamevault.checkout.domain.ports.in;

import java.util.Map;

public interface CheckoutUseCase {
    String createCheckoutSession(Long userId);
    void handlePaymentWebhook(String payload, Map<String, String> headers);
}
