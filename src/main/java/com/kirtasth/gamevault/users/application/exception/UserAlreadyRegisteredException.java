package com.kirtasth.gamevault.users.application.exception;

import com.kirtasth.gamevault.common.application.exception.ResourceConflictException;

public class UserAlreadyRegisteredException extends ResourceConflictException {
    public UserAlreadyRegisteredException() {
        super("User with email or username already registered:");
    }
}
