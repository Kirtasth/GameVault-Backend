package com.kirtasth.gamevault.users.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ApplicationException;

public class TokenStoreException extends ApplicationException {
    public TokenStoreException(Long userId) {
        super("Could not store token for user with id: " + userId + ".");
    }
}
