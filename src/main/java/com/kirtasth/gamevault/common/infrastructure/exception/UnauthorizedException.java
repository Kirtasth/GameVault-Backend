package com.kirtasth.gamevault.common.infrastructure.exception;

public class UnauthorizedException extends GameVaultException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
