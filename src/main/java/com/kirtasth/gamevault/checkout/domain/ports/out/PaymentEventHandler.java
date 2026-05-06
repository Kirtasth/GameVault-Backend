package com.kirtasth.gamevault.checkout.domain.ports.out;

import java.util.Map;

public interface PaymentEventHandler {
    void handleEvent(String payload, Map<String, String> headers);
    String getEventType();
}
