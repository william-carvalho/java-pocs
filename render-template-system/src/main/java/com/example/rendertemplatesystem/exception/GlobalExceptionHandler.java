package com.example.rendertemplatesystem.exception;

import com.example.rendertemplatesystem.dto.ErrorResponse;
import com.example.rendertemplatesystem.enums.RenderFormat;
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
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
                                                          HttpServletRequest request) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) cause;
            String fieldName = invalidFormatException.getPath().isEmpty()
                    ? "payload"
                    : invalidFormatException.getPath().get(0).getFieldName();

            if ("format".equals(fieldName)) {
                return build(HttpStatus.BAD_REQUEST, "Invalid request body",
                        "Invalid format. Supported values: " + joinFormats(), request.getRequestURI());
            }
        }

        return build(HttpStatus.BAD_REQUEST, "Invalid request body", "Malformed JSON request", request.getRequestURI());
    }

    @ExceptionHandler(TemplateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTemplateNotFound(TemplateNotFoundException ex,
                                                                HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Template not found", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidTemplateDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTemplateData(InvalidTemplateDataException ex,
                                                                   HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Invalid template data", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(TemplateRenderException.class)
    public ResponseEntity<ErrorResponse> handleRenderError(TemplateRenderException ex,
                                                           HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Template render failure", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                "Unexpected error while rendering template", request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message, String path) {
        ErrorResponse response = new ErrorResponse(status.value(), error, message, path, Instant.now());
        return ResponseEntity.status(status).body(response);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private String joinFormats() {
        return Arrays.stream(RenderFormat.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
