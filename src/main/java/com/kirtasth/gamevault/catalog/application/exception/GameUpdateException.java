package com.kirtasth.gamevault.catalog.application.exception;

import com.kirtasth.gamevault.common.application.exception.ResourceConflictException;

public class GameUpdateException extends ResourceConflictException {
    public GameUpdateException(Long gameId, String parameter) {
        super("Could not update game with id: " + gameId + ". Parameter: " + parameter + ".");
    }
}
