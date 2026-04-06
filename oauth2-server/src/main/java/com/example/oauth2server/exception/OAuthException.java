package com.example.oauth2server.exception;

import org.springframework.http.HttpStatus;

public class OAuthException extends RuntimeException {

    private final String error;
    private final HttpStatus status;

    public OAuthException(String error, String message, HttpStatus status) {
        super(message);
        this.error = error;
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
