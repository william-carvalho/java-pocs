package com.example.taxsystem.stresstest.api;

import com.example.taxsystem.stresstest.domain.StressJobStatus;

import java.time.LocalDateTime;

public class StressAsyncResponse {

    private final String executionId;
    private final StressJobStatus status;
    private final LocalDateTime startedAt;

    public StressAsyncResponse(String executionId, StressJobStatus status, LocalDateTime startedAt) {
        this.executionId = executionId;
        this.status = status;
        this.startedAt = startedAt;
    }

    public String getExecutionId() {
        return executionId;
    }

    public StressJobStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }
}
