package com.kirtasth.gamevault.catalog.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ResourceConflictException;

public class GameAlreadyExistsException extends ResourceConflictException {
    public GameAlreadyExistsException(Long gameId) {
        super("Game with id: " + gameId + " already exists.");
    }
}
