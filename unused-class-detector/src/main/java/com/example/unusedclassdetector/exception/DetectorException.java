package com.example.unusedclassdetector.exception;

public class DetectorException extends RuntimeException {

    public DetectorException(String message) {
        super(message);
    }

    public DetectorException(String message, Throwable cause) {
        super(message, cause);
    }
}

