package com.kirtasth.gamevault.common.infrastructure.exception;

public class ResourceNotFoundException extends GameVaultException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
