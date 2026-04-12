package com.kirtasth.gamevault.checkout.application.exceptions;

import com.kirtasth.gamevault.common.application.exception.ResourceNotFoundException;

public class GameKeyNotFoundException extends ResourceNotFoundException {
    public GameKeyNotFoundException(String message) {
        super(message);
    }
}
