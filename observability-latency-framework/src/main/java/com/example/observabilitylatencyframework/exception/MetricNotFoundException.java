package com.example.observabilitylatencyframework.exception;

public class MetricNotFoundException extends RuntimeException {

    public MetricNotFoundException(String operationName) {
        super("Metric not found for operation: " + operationName);
    }
}

