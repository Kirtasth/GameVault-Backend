package com.kirtasth.gamevault.checkout.application.exceptions;

import com.kirtasth.gamevault.common.application.exception.BadGatewayException;

public class StripeIntegrationException extends BadGatewayException {
    public StripeIntegrationException(String message) {
        super(message);
    }
}
