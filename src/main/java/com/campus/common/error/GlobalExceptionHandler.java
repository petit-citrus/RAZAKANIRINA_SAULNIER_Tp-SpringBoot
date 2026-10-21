package com.campus.common.error;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request) {
        return errorResponse(
                HttpStatus.NOT_FOUND,
                "RESSOURCE_INTROUVABLE",
                exception.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<ErrorResponse> handleBusinessConflict(
            BusinessConflictException exception,
            HttpServletRequest request) {
        return errorResponse(
                HttpStatus.CONFLICT,
                "CONFLIT_METIER",
                exception.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ECHOUEE",
                message,
                request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> errorResponse(
            HttpStatus status,
            String code,
            String message,
            String path) {
        String timestamp = OffsetDateTime.now(clock.withZone(ZoneOffset.UTC)).toString();
        ErrorResponse response = new ErrorResponse(
                timestamp,
                status.value(),
                code,
                message,
                path);
        return ResponseEntity.status(status).body(response);
    }
}
