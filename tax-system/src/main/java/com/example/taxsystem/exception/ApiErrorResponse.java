package com.example.taxsystem.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ApiErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private List<String> messages;

    public ApiErrorResponse(LocalDateTime timestamp, int status, String error, List<String> messages) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.messages = messages;
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

    public List<String> getMessages() {
        return messages;
    }
}
