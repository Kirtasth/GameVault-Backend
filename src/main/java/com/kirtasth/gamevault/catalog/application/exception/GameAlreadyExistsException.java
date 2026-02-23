package com.kirtasth.gamevault.catalog.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ApplicationException;

public class GameAlreadyExistsException extends ApplicationException {
    public GameAlreadyExistsException(Long gameId) {
        super("Game with id: " + gameId + " already exists.");
    }
}
