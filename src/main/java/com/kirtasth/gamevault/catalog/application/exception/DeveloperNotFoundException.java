package com.kirtasth.gamevault.catalog.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ResourceNotFoundException;

public class DeveloperNotFoundException extends ResourceNotFoundException {
    public DeveloperNotFoundException(Long devId) {
        super("Developer with id: " + devId + " not found");
    }
}
