package com.kirtasth.gamevault.common.infrastructure.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse (
        Instant timestamp,
        int errorCode,
        String error,
        String message,
        String path,

        //  Only include this field if it is not empty
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        Map<String, String> validationErrors
){
    public ErrorResponse(int errorCode, String error, String message, String path) {
        this(Instant.now(), errorCode, error, message, path, null);
    }

    public ErrorResponse(int errorCode, String error, String message, String path, Map<String, String> validationErrors) {
        this(Instant.now(), errorCode, error, message, path, validationErrors);
    }
}
