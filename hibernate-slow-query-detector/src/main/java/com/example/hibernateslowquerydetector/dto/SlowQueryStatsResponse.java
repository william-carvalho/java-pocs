package com.example.hibernateslowquerydetector.dto;

public class SlowQueryStatsResponse {

    private final long totalDetected;
    private final double avgExecutionTimeMs;
    private final long minExecutionTimeMs;
    private final long maxExecutionTimeMs;

    public SlowQueryStatsResponse(long totalDetected, double avgExecutionTimeMs, long minExecutionTimeMs, long maxExecutionTimeMs) {
        this.totalDetected = totalDetected;
        this.avgExecutionTimeMs = avgExecutionTimeMs;
        this.minExecutionTimeMs = minExecutionTimeMs;
        this.maxExecutionTimeMs = maxExecutionTimeMs;
    }

    public long getTotalDetected() {
        return totalDetected;
    }

    public double getAvgExecutionTimeMs() {
        return avgExecutionTimeMs;
    }

    public long getMinExecutionTimeMs() {
        return minExecutionTimeMs;
    }

    public long getMaxExecutionTimeMs() {
        return maxExecutionTimeMs;
    }
}

