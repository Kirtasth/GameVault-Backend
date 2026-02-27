package com.kirtasth.gamevault.catalog.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ResourceNotFoundException;

public class GameNotFoundException extends ResourceNotFoundException {
    public GameNotFoundException(Long gameId) {
        super("Game with id: " + gameId + " not found.");
    }
}
