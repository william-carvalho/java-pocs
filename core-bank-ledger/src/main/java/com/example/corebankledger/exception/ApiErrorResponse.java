package com.example.corebankledger.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ApiErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final List<String> details;

    public ApiErrorResponse(int status, String error, List<String> details) {
        this.status = status;
        this.error = error;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public List<String> getDetails() {
        return details;
    }
}

