package com.kirtasth.gamevault.users.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ApplicationException;

public class TokenRevokeException extends ApplicationException {
    public TokenRevokeException(Long userId) {
        super("Could not revoke all tokens for user with id: " + userId + ".");
    }
}
