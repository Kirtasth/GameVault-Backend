package com.kirtasth.gamevault.common.infrastructure.exception;

public abstract class GameVaultException extends RuntimeException {
    protected GameVaultException(String message) {
        super(message);
    }

}
