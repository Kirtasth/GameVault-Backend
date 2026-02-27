package com.kirtasth.gamevault.users.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String searchValue) {
        super("User with search value: " + searchValue + " not found.");
    }
}
