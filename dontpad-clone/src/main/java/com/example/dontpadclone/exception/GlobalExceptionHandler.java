package com.example.dontpadclone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found",
                        Collections.singletonList(exception.getMessage())));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), "Business Error",
                        Collections.singletonList(exception.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult().getAllErrors().stream()
                .map(error -> error instanceof FieldError
                        ? ((FieldError) error).getField() + ": " + error.getDefaultMessage()
                        : error.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation Error", details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                        Collections.singletonList(exception.getMessage())));
    }
}

