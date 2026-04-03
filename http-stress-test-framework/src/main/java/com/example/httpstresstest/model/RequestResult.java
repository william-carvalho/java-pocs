package com.example.httpstresstest.model;

import java.time.LocalDateTime;

public class RequestResult {

    private final boolean success;
    private final int statusCode;
    private final long latencyMs;
    private final String errorMessage;
    private final LocalDateTime startedAt;
    private final LocalDateTime finishedAt;

    public RequestResult(boolean success,
                         int statusCode,
                         long latencyMs,
                         String errorMessage,
                         LocalDateTime startedAt,
                         LocalDateTime finishedAt) {
        this.success = success;
        this.statusCode = statusCode;
        this.latencyMs = latencyMs;
        this.errorMessage = errorMessage;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }
}

