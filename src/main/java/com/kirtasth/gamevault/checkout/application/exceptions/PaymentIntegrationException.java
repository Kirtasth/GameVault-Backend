package com.kirtasth.gamevault.checkout.application.exceptions;

import com.kirtasth.gamevault.common.application.exception.BadGatewayException;

public class PaymentIntegrationException extends BadGatewayException {
    public PaymentIntegrationException(String message) {
        super(message);
    }
}
