package com.example.httpstresstest.metric;

public class LatencyStats {

    private final long minLatencyMs;
    private final long maxLatencyMs;
    private final double avgLatencyMs;
    private final long p50LatencyMs;
    private final long p95LatencyMs;

    public LatencyStats(long minLatencyMs,
                        long maxLatencyMs,
                        double avgLatencyMs,
                        long p50LatencyMs,
                        long p95LatencyMs) {
        this.minLatencyMs = minLatencyMs;
        this.maxLatencyMs = maxLatencyMs;
        this.avgLatencyMs = avgLatencyMs;
        this.p50LatencyMs = p50LatencyMs;
        this.p95LatencyMs = p95LatencyMs;
    }

    public long getMinLatencyMs() {
        return minLatencyMs;
    }

    public long getMaxLatencyMs() {
        return maxLatencyMs;
    }

    public double getAvgLatencyMs() {
        return avgLatencyMs;
    }

    public long getP50LatencyMs() {
        return p50LatencyMs;
    }

    public long getP95LatencyMs() {
        return p95LatencyMs;
    }
}

