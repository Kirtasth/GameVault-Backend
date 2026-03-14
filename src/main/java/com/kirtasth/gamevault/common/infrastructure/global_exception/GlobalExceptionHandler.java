package com.kirtasth.gamevault.common.infrastructure.global_exception;


import com.kirtasth.gamevault.common.application.exception.InternalServerException;
import com.kirtasth.gamevault.common.application.exception.ResourceConflictException;
import com.kirtasth.gamevault.common.application.exception.ResourceNotFoundException;
import com.kirtasth.gamevault.common.application.exception.UnauthorizedException;
import com.kirtasth.gamevault.common.infrastructure.dtos.responses.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;

@Slf4j
@ControllerAdvice(name = "gameVaultExceptionHandler", basePackages = "com.kirtasth.gamevault")
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

        var errorMap = new HashMap<String, String>();

        ex.getBindingResult().getFieldErrors().forEach(e ->
                errorMap.put(e.getField(), e.getDefaultMessage()));

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

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        var httpCode = HttpStatus.UNAUTHORIZED;
        var errorRes = new ErrorResponse(
                httpCode.value(),
                "Unauthorized",
                ex.getMessage());
        return new ResponseEntity<>(errorRes, httpCode);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        var httpCode = HttpStatus.UNAUTHORIZED;
        var errorRes = new ErrorResponse(
                httpCode.value(),
                "Unauthorized",
                ex.getMessage());
        log.warn("Unauthorized request: {} With cause: {}.",
                ex.getMessage(), ex.getCause() == null ? null : ex.getCause().getClass().getSimpleName());
        return new ResponseEntity<>(errorRes, httpCode);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        var httpCode = HttpStatus.NOT_FOUND;

        var errorRes = new ErrorResponse(
                httpCode.value(),
                "Not Found",
                ex.getMessage());
        return new ResponseEntity<>(errorRes, httpCode);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflictException(ResourceConflictException ex) {
        var httpCode = HttpStatus.CONFLICT;
        var errorRes = new ErrorResponse(
                httpCode.value(),
                "Conflict",
                ex.getMessage());
        log.warn("Conflict error: {} With cause: {}.",
                ex.getMessage(), ex.getCause() == null ? null : ex.getCause().getClass().getSimpleName());
        return new ResponseEntity<>(errorRes, httpCode);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerException(InternalServerException ex) {
        var httpCode = HttpStatus.INTERNAL_SERVER_ERROR;

        var errorRes = new ErrorResponse(
                httpCode.value(),
                "Internal Server Error",
                "Something went wrong in the server");
        log.error("Internal server controlled error: {} With cause: {}.",
                ex.getMessage(), ex.getCause() == null ? null : ex.getCause().getClass().getSimpleName(),
                ex);
        return new ResponseEntity<>(errorRes, httpCode);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        var httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
        var errorRes = new ErrorResponse(
                httpCode.value(),
                "Internal Server Error",
                "Something went wrong in the server");
        log.error("Internal server uncontrolled error: {} With cause: {}.",
                ex.getMessage(), ex.getCause() == null ? null : ex.getCause().getClass().getSimpleName(),
                ex);
        return new ResponseEntity<>(errorRes, httpCode);
    }
}
