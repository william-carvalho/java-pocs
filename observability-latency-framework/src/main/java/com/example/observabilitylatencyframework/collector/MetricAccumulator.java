package com.example.observabilitylatencyframework.collector;

import com.example.observabilitylatencyframework.metric.LatencyMetric;

public class MetricAccumulator {

    private long count;
    private long minLatencyMs;
    private long maxLatencyMs;
    private long totalLatencyMs;
    private long lastLatencyMs;
    private long errorCount;

    public synchronized void record(long latencyMs, boolean error) {
        count++;
        totalLatencyMs += latencyMs;
        lastLatencyMs = latencyMs;

        if (count == 1) {
            minLatencyMs = latencyMs;
            maxLatencyMs = latencyMs;
        } else {
            minLatencyMs = Math.min(minLatencyMs, latencyMs);
            maxLatencyMs = Math.max(maxLatencyMs, latencyMs);
        }

        if (error) {
            errorCount++;
        }
    }

    public synchronized LatencyMetric snapshot(String operationName) {
        double average = count == 0 ? 0.0d : (double) totalLatencyMs / count;
        return new LatencyMetric(
                operationName,
                count,
                minLatencyMs,
                maxLatencyMs,
                totalLatencyMs,
                lastLatencyMs,
                average,
                errorCount
        );
    }
}

