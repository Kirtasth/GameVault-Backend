package com.kirtasth.gamevault.checkout.application.exceptions;

import com.kirtasth.gamevault.common.application.exception.ResourceConflictException;

public class KeysNotAvailableException extends ResourceConflictException {
    public KeysNotAvailableException(String message) {
        super(message);
    }
}
