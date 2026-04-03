package com.example.httpstresstest.executor;

import com.example.httpstresstest.config.StressTestConfig;
import com.example.httpstresstest.http.HttpRequestExecutor;
import com.example.httpstresstest.metric.LatencyStats;
import com.example.httpstresstest.metric.MetricsCollector;
import com.example.httpstresstest.model.RequestResult;
import com.example.httpstresstest.report.StressTestReport;
import com.example.httpstresstest.util.ConfigValidator;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StressTestRunner {

    private final HttpRequestExecutor httpRequestExecutor;

    public StressTestRunner() {
        this(new HttpRequestExecutor());
    }

    public StressTestRunner(HttpRequestExecutor httpRequestExecutor) {
        this.httpRequestExecutor = httpRequestExecutor;
    }

    public StressTestReport run(StressTestConfig config) {
        ConfigValidator.validate(config);

        long startedAt = System.nanoTime();
        MetricsCollector collector = new MetricsCollector();
        ExecutorService executorService = Executors.newFixedThreadPool(config.getThreadPoolSize());
        CompletionService<RequestResult> completionService = new ExecutorCompletionService<RequestResult>(executorService);

        try {
            for (int index = 0; index < config.getTotalRequests(); index++) {
                completionService.submit(() -> httpRequestExecutor.execute(config));
            }

            for (int index = 0; index < config.getTotalRequests(); index++) {
                try {
                    collector.add(completionService.take().get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    collector.add(new RequestResult(false, -1, 0, "Interrupted while waiting for request completion", null, null));
                } catch (ExecutionException exception) {
                    collector.add(new RequestResult(false, -1, 0, exception.getCause() == null
                            ? exception.getMessage()
                            : exception.getCause().getMessage(), null, null));
                }
            }
        } finally {
            executorService.shutdownNow();
        }

        long totalDurationMs = (System.nanoTime() - startedAt) / 1_000_000L;
        LatencyStats latencyStats = collector.calculateLatencyStats();
        int successful = collector.getSuccessfulRequests();
        int failed = collector.getFailedRequests();
        double successRate = config.getTotalRequests() == 0
                ? 0.0d
                : (successful * 100.0d) / config.getTotalRequests();
        double requestsPerSecond = totalDurationMs == 0
                ? config.getTotalRequests()
                : (config.getTotalRequests() * 1000.0d) / totalDurationMs;

        return new StressTestReport(
                config.getUrl(),
                config.getMethod(),
                config.getTotalRequests(),
                successful,
                failed,
                successRate,
                latencyStats.getMinLatencyMs(),
                latencyStats.getMaxLatencyMs(),
                latencyStats.getAvgLatencyMs(),
                latencyStats.getP50LatencyMs(),
                latencyStats.getP95LatencyMs(),
                totalDurationMs,
                requestsPerSecond,
                collector.getStatusCodeCounts()
        );
    }
}

