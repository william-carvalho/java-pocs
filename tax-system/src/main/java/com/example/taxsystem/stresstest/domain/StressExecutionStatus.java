package com.example.taxsystem.stresstest.domain;

import java.time.LocalDateTime;

public class StressExecutionStatus {

    private final String executionId;
    private final StressJobStatus status;
    private final LocalDateTime startedAt;
    private final LocalDateTime completedAt;
    private final String message;
    private final StressTestReport report;

    private StressExecutionStatus(String executionId,
                                  StressJobStatus status,
                                  LocalDateTime startedAt,
                                  LocalDateTime completedAt,
                                  String message,
                                  StressTestReport report) {
        this.executionId = executionId;
        this.status = status;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.message = message;
        this.report = report;
    }

    public static StressExecutionStatus running(String executionId, LocalDateTime startedAt) {
        return new StressExecutionStatus(executionId, StressJobStatus.RUNNING, startedAt, null, "Execution is running", null);
    }

    public static StressExecutionStatus completed(StressTestReport report) {
        return new StressExecutionStatus(report.getExecutionId(), StressJobStatus.COMPLETED, report.getStartedAt(), report.getCompletedAt(),
                "Execution completed", report);
    }

    public static StressExecutionStatus failed(String executionId, LocalDateTime startedAt, String message) {
        return new StressExecutionStatus(executionId, StressJobStatus.FAILED, startedAt, LocalDateTime.now(), message, null);
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

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public String getMessage() {
        return message;
    }

    public StressTestReport getReport() {
        return report;
    }
}
