package com.example.httpserver.exception;

public class BadHttpRequestException extends RuntimeException {

    public BadHttpRequestException(String message) {
        super(message);
    }
}

