package com.kirtasth.gamevault.users.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ApplicationException;

public class UserAlreadyRegisteredException extends ApplicationException {
    public UserAlreadyRegisteredException() {
        super("User with email or username already registered:");
    }
}
