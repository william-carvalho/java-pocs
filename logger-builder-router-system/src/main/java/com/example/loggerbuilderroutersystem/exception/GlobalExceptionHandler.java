package com.example.loggerbuilderroutersystem.exception;

import com.example.loggerbuilderroutersystem.dto.ErrorResponse;
import com.example.loggerbuilderroutersystem.enums.LogDestination;
import com.example.loggerbuilderroutersystem.enums.LogLevel;
import com.example.loggerbuilderroutersystem.enums.LogMode;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));

        return build(HttpStatus.BAD_REQUEST, "Validation error", message, request.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException ex,
                                                                 HttpServletRequest request) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) cause;
            String fieldName = invalidFormatException.getPath().isEmpty()
                    ? "payload"
                    : invalidFormatException.getPath().get(0).getFieldName();
            return build(HttpStatus.BAD_REQUEST, "Invalid request body",
                    resolveEnumErrorMessage(fieldName), request.getRequestURI());
        }

        return build(HttpStatus.BAD_REQUEST, "Invalid request body", "Malformed JSON request", request.getRequestURI());
    }

    @ExceptionHandler(DestinationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDestinationNotFound(DestinationNotFoundException ex,
                                                                   HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Invalid destination", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(LogWriteException.class)
    public ResponseEntity<ErrorResponse> handleLogWrite(LogWriteException ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Log write failure", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                "Unexpected error while processing log request", request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message, String path) {
        ErrorResponse response = new ErrorResponse(status.value(), error, message, path, Instant.now());
        return ResponseEntity.status(status).body(response);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private String resolveEnumErrorMessage(String fieldName) {
        if ("destination".equals(fieldName)) {
            return "Invalid destination. Supported values: " + joinValues(LogDestination.values());
        }
        if ("mode".equals(fieldName)) {
            return "Invalid mode. Supported values: " + joinValues(LogMode.values());
        }
        if ("level".equals(fieldName)) {
            return "Invalid level. Supported values: " + joinValues(LogLevel.values());
        }
        return "Invalid value for field: " + fieldName;
    }

    private String joinValues(Enum<?>[] values) {
        return Arrays.stream(values)
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
