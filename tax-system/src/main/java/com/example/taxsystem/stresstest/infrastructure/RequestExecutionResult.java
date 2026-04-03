package com.example.taxsystem.stresstest.infrastructure;

public class RequestExecutionResult {

    private final boolean success;
    private final long latencyMillis;
    private final String message;

    public RequestExecutionResult(boolean success, long latencyMillis, String message) {
        this.success = success;
        this.latencyMillis = latencyMillis;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getLatencyMillis() {
        return latencyMillis;
    }

    public String getMessage() {
        return message;
    }
}
