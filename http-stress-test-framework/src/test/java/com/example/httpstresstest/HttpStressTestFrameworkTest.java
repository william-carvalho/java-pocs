package com.example.httpstresstest;

import com.example.httpstresstest.config.HttpMethod;
import com.example.httpstresstest.config.StressTestConfig;
import com.example.httpstresstest.demo.DemoHttpServer;
import com.example.httpstresstest.exception.StressTestException;
import com.example.httpstresstest.executor.StressTestRunner;
import com.example.httpstresstest.metric.LatencyStats;
import com.example.httpstresstest.metric.MetricsCollector;
import com.example.httpstresstest.model.RequestResult;
import com.example.httpstresstest.report.StressTestReport;
import com.example.httpstresstest.util.ConfigValidator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpStressTestFrameworkTest {

    @Test
    void shouldValidateConfig() {
        StressTestConfig config = StressTestConfig.builder()
                .url("http://localhost:8080/health")
                .method(HttpMethod.GET)
                .totalRequests(10)
                .threadPoolSize(2)
                .connectTimeoutMs(1000)
                .readTimeoutMs(1000)
                .build();

        ConfigValidator.validate(config);
    }

    @Test
    void shouldRejectInvalidConfig() {
        StressTestConfig config = StressTestConfig.builder()
                .url(" ")
                .method(HttpMethod.GET)
                .totalRequests(0)
                .threadPoolSize(0)
                .build();

        assertThrows(StressTestException.class, () -> ConfigValidator.validate(config));
    }

    @Test
    void shouldCalculateLatencyStatsAndPercentiles() {
        MetricsCollector collector = new MetricsCollector();
        collector.add(result(true, 200, 10));
        collector.add(result(true, 200, 20));
        collector.add(result(true, 200, 30));
        collector.add(result(false, 500, 40));
        collector.add(result(true, 200, 50));

        LatencyStats stats = collector.calculateLatencyStats();

        assertEquals(10, stats.getMinLatencyMs());
        assertEquals(50, stats.getMaxLatencyMs());
        assertEquals(30.0d, stats.getAvgLatencyMs());
        assertEquals(30, stats.getP50LatencyMs());
        assertEquals(50, stats.getP95LatencyMs());
    }

    @Test
    void shouldConsolidateFailuresAndStatusCodes() throws Exception {
        DemoHttpServer server = new DemoHttpServer(9191);
        server.start();
        try {
            StressTestConfig config = StressTestConfig.builder()
                    .url("http://localhost:9191/unknown")
                    .method(HttpMethod.GET)
                    .totalRequests(20)
                    .threadPoolSize(4)
                    .connectTimeoutMs(1000)
                    .readTimeoutMs(1000)
                    .build();

            StressTestReport report = new StressTestRunner().run(config);

            assertEquals(20, report.getTotalRequests());
            assertEquals(0, report.getSuccessfulRequests());
            assertEquals(20, report.getFailedRequests());
            assertEquals(Integer.valueOf(20), report.getStatusCodeCounts().get(404));
        } finally {
            server.stop();
        }
    }

    @Test
    void shouldExecuteSuccessfulIntegrationRun() throws Exception {
        DemoHttpServer server = new DemoHttpServer(9192);
        server.start();
        try {
            StressTestConfig config = StressTestConfig.builder()
                    .url("http://localhost:9192/health")
                    .method(HttpMethod.GET)
                    .totalRequests(25)
                    .threadPoolSize(5)
                    .connectTimeoutMs(1000)
                    .readTimeoutMs(1000)
                    .build();

            StressTestReport report = new StressTestRunner().run(config);

            assertEquals(25, report.getTotalRequests());
            assertEquals(25, report.getSuccessfulRequests());
            assertEquals(0, report.getFailedRequests());
            assertTrue(report.getRequestsPerSecond() > 0);
            assertEquals(Integer.valueOf(25), report.getStatusCodeCounts().get(200));
        } finally {
            server.stop();
        }
    }

    private RequestResult result(boolean success, int statusCode, long latencyMs) {
        return new RequestResult(success, statusCode, latencyMs, null, LocalDateTime.now(), LocalDateTime.now());
    }
}

