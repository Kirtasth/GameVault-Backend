package com.kirtasth.gamevault.catalog.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ApplicationException;

public class GameNotFoundException extends ApplicationException {
    public GameNotFoundException(Long gameId) {
        super("Game with id: " + gameId + " not found.");
    }
}
