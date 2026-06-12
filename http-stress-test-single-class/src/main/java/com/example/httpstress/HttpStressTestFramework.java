package com.example.httpstress;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class HttpStressTestFramework {
    private HttpStressTestFramework() {
    }

    public static StressTestReport run(StressTestConfig config) {
        return new StressTestRunner(new UrlConnectionRequestExecutor()).run(config);
    }

    public enum HttpMethod {
        GET,
        POST
    }

    public interface RequestExecutor {
        RequestResult execute(StressTestConfig config);
    }

    public static final class StressTestRunner {
        private final RequestExecutor requestExecutor;

        public StressTestRunner(RequestExecutor requestExecutor) {
            if (requestExecutor == null) {
                throw new IllegalArgumentException("requestExecutor is required");
            }
            this.requestExecutor = requestExecutor;
        }

        public StressTestReport run(final StressTestConfig config) {
            config.validate();
            ExecutorService executor = Executors.newFixedThreadPool(config.getThreadPoolSize());
            MetricsCollector collector = new MetricsCollector();
            long startedAt = System.nanoTime();
            try {
                List<Future<RequestResult>> futures = new ArrayList<Future<RequestResult>>();
                for (int index = 0; index < config.getTotalRequests(); index++) {
                    futures.add(executor.submit(new Callable<RequestResult>() {
                        public RequestResult call() {
                            return requestExecutor.execute(config);
                        }
                    }));
                }
                for (Future<RequestResult> future : futures) {
                    try {
                        collector.record(future.get());
                    } catch (Exception ex) {
                        collector.record(RequestResult.failure(-1, 0L, ex.getMessage()));
                    }
                }
            } finally {
                executor.shutdownNow();
            }
            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
            return collector.report(config, durationMs);
        }
    }

    public static final class UrlConnectionRequestExecutor implements RequestExecutor {
        public RequestResult execute(StressTestConfig config) {
            long startedAt = System.nanoTime();
            HttpURLConnection connection = null;
            try {
                URL url = new URL(config.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(config.getMethod().name());
                connection.setConnectTimeout(config.getConnectTimeoutMs());
                connection.setReadTimeout(config.getReadTimeoutMs());
                if (config.getMethod() == HttpMethod.POST) {
                    connection.setDoOutput(true);
                    byte[] body = config.getBody().getBytes(StandardCharsets.UTF_8);
                    connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                    connection.setRequestProperty("Content-Length", String.valueOf(body.length));
                    OutputStream output = connection.getOutputStream();
                    output.write(body);
                    output.flush();
                    output.close();
                }
                int statusCode = connection.getResponseCode();
                long latencyMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
                return RequestResult.completed(statusCode, latencyMs);
            } catch (Exception ex) {
                long latencyMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
                return RequestResult.failure(-1, latencyMs, ex.getClass().getSimpleName() + ": " + ex.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

    public static final class StressTestConfig {
        private final String url;
        private final HttpMethod method;
        private final int totalRequests;
        private final int threadPoolSize;
        private final int connectTimeoutMs;
        private final int readTimeoutMs;
        private final String body;

        private StressTestConfig(Builder builder) {
            this.url = builder.url;
            this.method = builder.method;
            this.totalRequests = builder.totalRequests;
            this.threadPoolSize = builder.threadPoolSize;
            this.connectTimeoutMs = builder.connectTimeoutMs;
            this.readTimeoutMs = builder.readTimeoutMs;
            this.body = builder.body == null ? "" : builder.body;
            validate();
        }

        public static Builder builder() {
            return new Builder();
        }

        private void validate() {
            if (isBlank(url)) {
                throw new IllegalArgumentException("url is required");
            }
            if (method == null) {
                throw new IllegalArgumentException("method is required");
            }
            if (totalRequests <= 0) {
                throw new IllegalArgumentException("totalRequests must be greater than zero");
            }
            if (threadPoolSize <= 0) {
                throw new IllegalArgumentException("threadPoolSize must be greater than zero");
            }
            if (connectTimeoutMs < 0 || readTimeoutMs < 0) {
                throw new IllegalArgumentException("timeouts must be zero or greater");
            }
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

        public int getThreadPoolSize() {
            return threadPoolSize;
        }

        public int getConnectTimeoutMs() {
            return connectTimeoutMs;
        }

        public int getReadTimeoutMs() {
            return readTimeoutMs;
        }

        public String getBody() {
            return body;
        }

        public static final class Builder {
            private String url;
            private HttpMethod method = HttpMethod.GET;
            private int totalRequests = 1;
            private int threadPoolSize = 1;
            private int connectTimeoutMs = 3000;
            private int readTimeoutMs = 3000;
            private String body;

            public Builder url(String url) {
                this.url = url;
                return this;
            }

            public Builder method(HttpMethod method) {
                this.method = method;
                return this;
            }

            public Builder totalRequests(int totalRequests) {
                this.totalRequests = totalRequests;
                return this;
            }

            public Builder threadPoolSize(int threadPoolSize) {
                this.threadPoolSize = threadPoolSize;
                return this;
            }

            public Builder connectTimeoutMs(int connectTimeoutMs) {
                this.connectTimeoutMs = connectTimeoutMs;
                return this;
            }

            public Builder readTimeoutMs(int readTimeoutMs) {
                this.readTimeoutMs = readTimeoutMs;
                return this;
            }

            public Builder body(String body) {
                this.body = body;
                return this;
            }

            public StressTestConfig build() {
                return new StressTestConfig(this);
            }
        }
    }

    public static final class RequestResult {
        private final int statusCode;
        private final long latencyMs;
        private final boolean success;
        private final String errorMessage;

        private RequestResult(int statusCode, long latencyMs, boolean success, String errorMessage) {
            this.statusCode = statusCode;
            this.latencyMs = latencyMs;
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public static RequestResult completed(int statusCode, long latencyMs) {
            return new RequestResult(statusCode, latencyMs, statusCode >= 200 && statusCode < 300, null);
        }

        public static RequestResult failure(int statusCode, long latencyMs, String errorMessage) {
            return new RequestResult(statusCode, latencyMs, false, errorMessage);
        }

        public int getStatusCode() {
            return statusCode;
        }

        public long getLatencyMs() {
            return latencyMs;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public static final class MetricsCollector {
        private final List<RequestResult> results = new ArrayList<RequestResult>();

        public synchronized void record(RequestResult result) {
            results.add(result);
        }

        public StressTestReport report(StressTestConfig config, long totalDurationMs) {
            List<Long> latencies = new ArrayList<Long>();
            Map<Integer, Integer> statusCounts = new TreeMap<Integer, Integer>();
            int successCount = 0;
            int failureCount = 0;
            for (RequestResult result : results) {
                latencies.add(result.getLatencyMs());
                Integer current = statusCounts.get(result.getStatusCode());
                statusCounts.put(result.getStatusCode(), current == null ? 1 : current + 1);
                if (result.isSuccess()) {
                    successCount++;
                } else {
                    failureCount++;
                }
            }
            Collections.sort(latencies);
            return new StressTestReport(
                    config,
                    results.size(),
                    successCount,
                    failureCount,
                    min(latencies),
                    max(latencies),
                    average(latencies),
                    percentile(latencies, 50),
                    percentile(latencies, 95),
                    totalDurationMs,
                    statusCounts);
        }

        private static long min(List<Long> values) {
            return values.isEmpty() ? 0L : values.get(0);
        }

        private static long max(List<Long> values) {
            return values.isEmpty() ? 0L : values.get(values.size() - 1);
        }

        private static double average(List<Long> values) {
            if (values.isEmpty()) {
                return 0.0;
            }
            long total = 0L;
            for (Long value : values) {
                total += value;
            }
            return total / (double) values.size();
        }

        private static long percentile(List<Long> values, int percentile) {
            if (values.isEmpty()) {
                return 0L;
            }
            int index = (int) Math.ceil((percentile / 100.0) * values.size()) - 1;
            if (index < 0) {
                index = 0;
            }
            if (index >= values.size()) {
                index = values.size() - 1;
            }
            return values.get(index);
        }
    }

    public static final class StressTestReport {
        private final StressTestConfig config;
        private final int totalRequests;
        private final int successfulRequests;
        private final int failedRequests;
        private final long minLatencyMs;
        private final long maxLatencyMs;
        private final double averageLatencyMs;
        private final long p50LatencyMs;
        private final long p95LatencyMs;
        private final long totalDurationMs;
        private final Map<Integer, Integer> statusCodeCounts;

        private StressTestReport(StressTestConfig config,
                                 int totalRequests,
                                 int successfulRequests,
                                 int failedRequests,
                                 long minLatencyMs,
                                 long maxLatencyMs,
                                 double averageLatencyMs,
                                 long p50LatencyMs,
                                 long p95LatencyMs,
                                 long totalDurationMs,
                                 Map<Integer, Integer> statusCodeCounts) {
            this.config = config;
            this.totalRequests = totalRequests;
            this.successfulRequests = successfulRequests;
            this.failedRequests = failedRequests;
            this.minLatencyMs = minLatencyMs;
            this.maxLatencyMs = maxLatencyMs;
            this.averageLatencyMs = averageLatencyMs;
            this.p50LatencyMs = p50LatencyMs;
            this.p95LatencyMs = p95LatencyMs;
            this.totalDurationMs = totalDurationMs;
            this.statusCodeCounts = Collections.unmodifiableMap(new TreeMap<Integer, Integer>(statusCodeCounts));
        }

        public double getSuccessRate() {
            return totalRequests == 0 ? 0.0 : (successfulRequests * 100.0) / totalRequests;
        }

        public double getRequestsPerSecond() {
            if (totalDurationMs <= 0) {
                return totalRequests;
            }
            return totalRequests / (totalDurationMs / 1000.0);
        }

        public String toTextReport() {
            StringBuilder text = new StringBuilder();
            text.append("Stress Test Report\n");
            text.append("URL: ").append(config.getUrl()).append("\n");
            text.append("Method: ").append(config.getMethod()).append("\n");
            text.append("Total Requests: ").append(totalRequests).append("\n");
            text.append("Successful Requests: ").append(successfulRequests).append("\n");
            text.append("Failed Requests: ").append(failedRequests).append("\n");
            text.append(String.format("Success Rate: %.2f%%\n", getSuccessRate()));
            text.append("Min Latency: ").append(minLatencyMs).append(" ms\n");
            text.append("Max Latency: ").append(maxLatencyMs).append(" ms\n");
            text.append(String.format("Avg Latency: %.2f ms\n", averageLatencyMs));
            text.append("P50 Latency: ").append(p50LatencyMs).append(" ms\n");
            text.append("P95 Latency: ").append(p95LatencyMs).append(" ms\n");
            text.append("Total Duration: ").append(totalDurationMs).append(" ms\n");
            text.append(String.format("Requests/sec: %.2f\n", getRequestsPerSecond()));
            text.append("Status Codes: ").append(statusCodeCounts);
            return text.toString();
        }

        public StressTestConfig getConfig() {
            return config;
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

        public long getMinLatencyMs() {
            return minLatencyMs;
        }

        public long getMaxLatencyMs() {
            return maxLatencyMs;
        }

        public double getAverageLatencyMs() {
            return averageLatencyMs;
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

        public Map<Integer, Integer> getStatusCodeCounts() {
            return statusCodeCounts;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
