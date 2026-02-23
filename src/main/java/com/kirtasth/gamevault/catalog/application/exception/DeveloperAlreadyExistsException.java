package com.kirtasth.gamevault.catalog.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ApplicationException;

public class DeveloperAlreadyExistsException extends ApplicationException {
    public DeveloperAlreadyExistsException(String developerName) {
        super("Developer with name " + developerName + " already exists");
    }
}
