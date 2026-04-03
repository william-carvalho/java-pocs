package com.example.taxsystem.stresstest.domain;

import org.springframework.http.HttpMethod;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StressTestReport {

    private final String executionId;
    private final String url;
    private final HttpMethod method;
    private final StressExecutionMode mode;
    private final int totalRequests;
    private final int successCount;
    private final int errorCount;
    private final long averageLatencyMillis;
    private final long minLatencyMillis;
    private final long maxLatencyMillis;
    private final long p50LatencyMillis;
    private final long p95LatencyMillis;
    private final double throughputPerSecond;
    private final long durationMillis;
    private final LocalDateTime startedAt;
    private final LocalDateTime completedAt;
    private final List<String> sampleErrors;

    private StressTestReport(Builder builder) {
        this.executionId = builder.executionId;
        this.url = builder.url;
        this.method = builder.method;
        this.mode = builder.mode;
        this.totalRequests = builder.totalRequests;
        this.successCount = builder.successCount;
        this.errorCount = builder.errorCount;
        this.averageLatencyMillis = builder.averageLatencyMillis;
        this.minLatencyMillis = builder.minLatencyMillis;
        this.maxLatencyMillis = builder.maxLatencyMillis;
        this.p50LatencyMillis = builder.p50LatencyMillis;
        this.p95LatencyMillis = builder.p95LatencyMillis;
        this.throughputPerSecond = builder.throughputPerSecond;
        this.durationMillis = builder.durationMillis;
        this.startedAt = builder.startedAt;
        this.completedAt = builder.completedAt;
        this.sampleErrors = Collections.unmodifiableList(new ArrayList<String>(builder.sampleErrors));
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public StressExecutionMode getMode() {
        return mode;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public long getAverageLatencyMillis() {
        return averageLatencyMillis;
    }

    public long getMinLatencyMillis() {
        return minLatencyMillis;
    }

    public long getMaxLatencyMillis() {
        return maxLatencyMillis;
    }

    public long getP50LatencyMillis() {
        return p50LatencyMillis;
    }

    public long getP95LatencyMillis() {
        return p95LatencyMillis;
    }

    public double getThroughputPerSecond() {
        return throughputPerSecond;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public List<String> getSampleErrors() {
        return sampleErrors;
    }

    public static class Builder {
        private String executionId;
        private String url;
        private HttpMethod method;
        private StressExecutionMode mode;
        private int totalRequests;
        private int successCount;
        private int errorCount;
        private long averageLatencyMillis;
        private long minLatencyMillis;
        private long maxLatencyMillis;
        private long p50LatencyMillis;
        private long p95LatencyMillis;
        private double throughputPerSecond;
        private long durationMillis;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private List<String> sampleErrors = new ArrayList<String>();

        public Builder executionId(String executionId) {
            this.executionId = executionId;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder mode(StressExecutionMode mode) {
            this.mode = mode;
            return this;
        }

        public Builder totalRequests(int totalRequests) {
            this.totalRequests = totalRequests;
            return this;
        }

        public Builder successCount(int successCount) {
            this.successCount = successCount;
            return this;
        }

        public Builder errorCount(int errorCount) {
            this.errorCount = errorCount;
            return this;
        }

        public Builder averageLatencyMillis(long averageLatencyMillis) {
            this.averageLatencyMillis = averageLatencyMillis;
            return this;
        }

        public Builder minLatencyMillis(long minLatencyMillis) {
            this.minLatencyMillis = minLatencyMillis;
            return this;
        }

        public Builder maxLatencyMillis(long maxLatencyMillis) {
            this.maxLatencyMillis = maxLatencyMillis;
            return this;
        }

        public Builder p50LatencyMillis(long p50LatencyMillis) {
            this.p50LatencyMillis = p50LatencyMillis;
            return this;
        }

        public Builder p95LatencyMillis(long p95LatencyMillis) {
            this.p95LatencyMillis = p95LatencyMillis;
            return this;
        }

        public Builder throughputPerSecond(double throughputPerSecond) {
            this.throughputPerSecond = throughputPerSecond;
            return this;
        }

        public Builder durationMillis(long durationMillis) {
            this.durationMillis = durationMillis;
            return this;
        }

        public Builder startedAt(LocalDateTime startedAt) {
            this.startedAt = startedAt;
            return this;
        }

        public Builder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public Builder sampleErrors(List<String> sampleErrors) {
            this.sampleErrors = sampleErrors;
            return this;
        }

        public StressTestReport build() {
            return new StressTestReport(this);
        }
    }
}
