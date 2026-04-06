package com.example.oauth2server.exception;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.oauth2server.dto.OAuthErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<OAuthErrorResponse> handleOAuthException(OAuthException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(new OAuthErrorResponse(exception.getError(), exception.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException exception,
                                                           HttpServletRequest request) {
        return buildApiError(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception,
                                                             HttpServletRequest request) {
        String message = "Validation failed";
        if (!exception.getBindingResult().getFieldErrors().isEmpty()) {
            FieldError error = exception.getBindingResult().getFieldErrors().get(0);
            message = error.getField() + ": " + error.getDefaultMessage();
        }
        return buildApiError(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException exception,
                                                                  HttpServletRequest request) {
        return buildApiError(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception exception, HttpServletRequest request) {
        return buildApiError(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponse> buildApiError(HttpStatus status, String message, String path) {
        ApiErrorResponse response = new ApiErrorResponse(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), message, path);
        return ResponseEntity.status(status).body(response);
    }
}
