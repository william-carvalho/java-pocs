package com.example.taxsystem.common.config;

import java.time.LocalDateTime;
import java.util.UUID;

public class StressExecutionContext {

    private final String executionId = UUID.randomUUID().toString();
    private final LocalDateTime createdAt = LocalDateTime.now();

    public String getExecutionId() {
        return executionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
