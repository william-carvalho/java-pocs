package com.example.observabilitylatencysingleclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@RestController
public class ObservabilityLatencySingleClassApplication {

    private final LatencyFramework framework = new LatencyFramework();

    public static void main(String[] args) {
        SpringApplication.run(ObservabilityLatencySingleClassApplication.class, args);
    }

    @PostMapping("/latencies")
    public LatencySummary record(@RequestBody LatencyRequest request) {
        if (request == null || blank(request.operation) || request.latencyMillis < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "operation and non-negative latencyMillis are required");
        }
        return framework.record(request.operation, request.latencyMillis);
    }

    @GetMapping("/latencies/{operation}")
    public LatencySummary summary(@PathVariable String operation) {
        LatencySummary summary = framework.summary(operation);
        if (summary == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "operation not found: " + operation);
        }
        return summary;
    }

    @GetMapping("/latencies")
    public List<LatencySummary> summaries() {
        return framework.summaries();
    }

    @GetMapping("/health/latency")
    public Map<String, Object> latencyHealth() {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put("status", framework.isHealthy(500) ? "UP" : "DEGRADED");
        response.put("maxAllowedAverageMillis", 500);
        response.put("metrics", framework.summaries());
        return response;
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public LatencyFramework getFramework() {
        return framework;
    }

    public static class LatencyRequest {
        public String operation;
        public long latencyMillis;
    }

    public static class LatencyFramework {
        private final Map<String, LatencyMetric> metrics = new ConcurrentHashMap<String, LatencyMetric>();

        public LatencySummary record(String operation, long latencyMillis) {
            if (operation == null || operation.trim().isEmpty()) {
                throw new IllegalArgumentException("operation is required");
            }
            if (latencyMillis < 0) {
                throw new IllegalArgumentException("latencyMillis must be non-negative");
            }

            String key = operation.trim();
            LatencyMetric metric = metrics.get(key);
            if (metric == null) {
                LatencyMetric created = new LatencyMetric(key);
                LatencyMetric existing = metrics.putIfAbsent(key, created);
                metric = existing == null ? created : existing;
            }

            metric.record(latencyMillis);
            return metric.summary();
        }

        public <T> T observe(String operation, Callable<T> callable) throws Exception {
            long start = System.nanoTime();
            try {
                return callable.call();
            } finally {
                long latencyMillis = (System.nanoTime() - start) / 1000000;
                record(operation, latencyMillis);
            }
        }

        public LatencySummary summary(String operation) {
            LatencyMetric metric = metrics.get(operation);
            return metric == null ? null : metric.summary();
        }

        public List<LatencySummary> summaries() {
            List<LatencySummary> result = new ArrayList<LatencySummary>();
            for (LatencyMetric metric : metrics.values()) {
                result.add(metric.summary());
            }
            Collections.sort(result, Comparator.comparing(LatencySummary::getOperation));
            return result;
        }

        public boolean isHealthy(long maxAllowedAverageMillis) {
            for (LatencySummary summary : summaries()) {
                if (summary.averageMillis > maxAllowedAverageMillis) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class LatencyMetric {
        private final String operation;
        private long count;
        private long totalMillis;
        private long minMillis = Long.MAX_VALUE;
        private long maxMillis;
        private LocalDateTime lastRecordedAt;

        public LatencyMetric(String operation) {
            this.operation = operation;
        }

        public synchronized void record(long latencyMillis) {
            count++;
            totalMillis += latencyMillis;
            minMillis = Math.min(minMillis, latencyMillis);
            maxMillis = Math.max(maxMillis, latencyMillis);
            lastRecordedAt = LocalDateTime.now();
        }

        public synchronized LatencySummary summary() {
            return new LatencySummary(
                    operation,
                    count,
                    count == 0 ? 0 : totalMillis / count,
                    count == 0 ? 0 : minMillis,
                    maxMillis,
                    totalMillis,
                    lastRecordedAt
            );
        }
    }

    public static class LatencySummary {
        public String operation;
        public long count;
        public long averageMillis;
        public long minMillis;
        public long maxMillis;
        public long totalMillis;
        public LocalDateTime lastRecordedAt;

        public LatencySummary(String operation,
                              long count,
                              long averageMillis,
                              long minMillis,
                              long maxMillis,
                              long totalMillis,
                              LocalDateTime lastRecordedAt) {
            this.operation = operation;
            this.count = count;
            this.averageMillis = averageMillis;
            this.minMillis = minMillis;
            this.maxMillis = maxMillis;
            this.totalMillis = totalMillis;
            this.lastRecordedAt = lastRecordedAt;
        }

        public String getOperation() {
            return operation;
        }
    }
}
