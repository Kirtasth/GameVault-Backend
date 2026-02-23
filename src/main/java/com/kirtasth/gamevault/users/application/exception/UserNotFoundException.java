package com.kirtasth.gamevault.users.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ApplicationException;

public class UserNotFoundException extends ApplicationException {
    public UserNotFoundException(String searchValue) {
        super("User with search value: " + searchValue + " not found.");
    }
}
