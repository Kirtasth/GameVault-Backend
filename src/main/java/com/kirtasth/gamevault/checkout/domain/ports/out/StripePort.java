package com.kirtasth.gamevault.checkout.domain.ports.out;

public interface StripePort {
    void handleEvent(String payload, String sigHeader);
    String getEventType();
}
