package com.kirtasth.gamevault.common.infrastructure.exception;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
