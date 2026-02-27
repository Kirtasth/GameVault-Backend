package com.kirtasth.gamevault.catalog.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ResourceConflictException;

public class DeveloperAlreadyExistsException extends ResourceConflictException {
    public DeveloperAlreadyExistsException(String developerName) {
        super("Developer with username " + developerName + " already exists");
    }
}
