package com.kirtasth.gamevault.common.application.exception;

public abstract class GameVaultException extends RuntimeException {
    protected GameVaultException(String message) {
        super(message);
    }

}
