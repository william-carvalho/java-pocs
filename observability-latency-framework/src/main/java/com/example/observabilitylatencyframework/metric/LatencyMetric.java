package com.example.observabilitylatencyframework.metric;

public class LatencyMetric {

    private final String operationName;
    private final long count;
    private final long minLatencyMs;
    private final long maxLatencyMs;
    private final long totalLatencyMs;
    private final long lastLatencyMs;
    private final double avgLatencyMs;
    private final long errorCount;

    public LatencyMetric(String operationName,
                         long count,
                         long minLatencyMs,
                         long maxLatencyMs,
                         long totalLatencyMs,
                         long lastLatencyMs,
                         double avgLatencyMs,
                         long errorCount) {
        this.operationName = operationName;
        this.count = count;
        this.minLatencyMs = minLatencyMs;
        this.maxLatencyMs = maxLatencyMs;
        this.totalLatencyMs = totalLatencyMs;
        this.lastLatencyMs = lastLatencyMs;
        this.avgLatencyMs = avgLatencyMs;
        this.errorCount = errorCount;
    }

    public String getOperationName() {
        return operationName;
    }

    public long getCount() {
        return count;
    }

    public long getMinLatencyMs() {
        return minLatencyMs;
    }

    public long getMaxLatencyMs() {
        return maxLatencyMs;
    }

    public long getTotalLatencyMs() {
        return totalLatencyMs;
    }

    public long getLastLatencyMs() {
        return lastLatencyMs;
    }

    public double getAvgLatencyMs() {
        return avgLatencyMs;
    }

    public long getErrorCount() {
        return errorCount;
    }

    @Override
    public String toString() {
        return "LatencyMetric{" +
                "operationName='" + operationName + '\'' +
                ", count=" + count +
                ", minLatencyMs=" + minLatencyMs +
                ", maxLatencyMs=" + maxLatencyMs +
                ", totalLatencyMs=" + totalLatencyMs +
                ", lastLatencyMs=" + lastLatencyMs +
                ", avgLatencyMs=" + avgLatencyMs +
                ", errorCount=" + errorCount +
                '}';
    }
}

