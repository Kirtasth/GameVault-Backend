package com.kirtasth.gamevault.auth.infrastructure.global_exceptions;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirtasth.gamevault.common.infrastructure.responses.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ControllerAdvice(name = "gameVaultExceptionHandler", basePackages = "com.kirtasth.gamevault")
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

        var errorMap = new HashMap<String, String>();

        ex.getBindingResult().getFieldErrors().forEach(e -> {
            errorMap.put(e.getField(), e.getDefaultMessage());
        });

        var httpCode = HttpStatus.BAD_REQUEST;
        var errorRes = new ErrorResponse(
                httpCode.value(),
                "Validation error",
                "Some or more fields of the request are invalid",
                errorMap);
        return new ResponseEntity<>(errorRes, httpCode);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        var errors = List.of(ex.getMessage());

        var httpCode = HttpStatus.BAD_REQUEST;
        var errorRes = new ErrorResponse(
                httpCode.value(),
                Strings.join(errors, '.'),
                null);
        return new ResponseEntity<>(errorRes, httpCode);
    }
}
