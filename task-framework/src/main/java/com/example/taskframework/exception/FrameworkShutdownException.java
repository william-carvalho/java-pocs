package com.example.taskframework.exception;

public class FrameworkShutdownException extends RuntimeException {

    public FrameworkShutdownException(String message) {
        super(message);
    }
}

