package com.kirtasth.gamevault.common.infrastructure.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timestamp,
        int errorCode,
        String error,
        String message,

        //  Only include this field if it is not empty
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        Map<String, String> validationErrors
) {
    public ErrorResponse(int errorCode, String error, String message) {
        this(Instant.now(), errorCode, error, message, null);
    }

    public ErrorResponse(int errorCode, String error, String message, Map<String, String> validationErrors) {
        this(Instant.now(), errorCode, error, message, validationErrors);
    }
}
