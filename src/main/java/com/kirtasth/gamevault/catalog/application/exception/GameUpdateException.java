package com.kirtasth.gamevault.catalog.application.exception;

import com.kirtasth.gamevault.common.infrastructure.exception.ApplicationException;

public class GameUpdateException extends ApplicationException {
    public GameUpdateException(Long gameId, String parameter) {
        super("Could not update game with id: " + gameId + ". Parameter: " + parameter + ".");
    }
}
