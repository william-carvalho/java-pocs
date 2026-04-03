package com.example.httpstresstest.report;

import com.example.httpstresstest.config.HttpMethod;

import java.util.Map;

public class StressTestReport {

    private final String url;
    private final HttpMethod method;
    private final int totalRequests;
    private final int successfulRequests;
    private final int failedRequests;
    private final double successRate;
    private final long minLatencyMs;
    private final long maxLatencyMs;
    private final double avgLatencyMs;
    private final long p50LatencyMs;
    private final long p95LatencyMs;
    private final long totalDurationMs;
    private final double requestsPerSecond;
    private final Map<Integer, Integer> statusCodeCounts;

    public StressTestReport(String url,
                            HttpMethod method,
                            int totalRequests,
                            int successfulRequests,
                            int failedRequests,
                            double successRate,
                            long minLatencyMs,
                            long maxLatencyMs,
                            double avgLatencyMs,
                            long p50LatencyMs,
                            long p95LatencyMs,
                            long totalDurationMs,
                            double requestsPerSecond,
                            Map<Integer, Integer> statusCodeCounts) {
        this.url = url;
        this.method = method;
        this.totalRequests = totalRequests;
        this.successfulRequests = successfulRequests;
        this.failedRequests = failedRequests;
        this.successRate = successRate;
        this.minLatencyMs = minLatencyMs;
        this.maxLatencyMs = maxLatencyMs;
        this.avgLatencyMs = avgLatencyMs;
        this.p50LatencyMs = p50LatencyMs;
        this.p95LatencyMs = p95LatencyMs;
        this.totalDurationMs = totalDurationMs;
        this.requestsPerSecond = requestsPerSecond;
        this.statusCodeCounts = statusCodeCounts;
    }

    public String getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public int getSuccessfulRequests() {
        return successfulRequests;
    }

    public int getFailedRequests() {
        return failedRequests;
    }

    public double getSuccessRate() {
        return successRate;
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

    public long getTotalDurationMs() {
        return totalDurationMs;
    }

    public double getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public Map<Integer, Integer> getStatusCodeCounts() {
        return statusCodeCounts;
    }
}

