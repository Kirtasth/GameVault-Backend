package com.kirtasth.gamevault.users.application.exception;

import com.kirtasth.gamevault.common.application.exception.ResourceConflictException;

public class TokenInvalidException extends ResourceConflictException {
    public TokenInvalidException(String message) {
        super(message);
    }
}
