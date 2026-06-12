package com.example.httpstress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.httpstress.HttpStressTestFramework.HttpMethod.GET;
import static com.example.httpstress.HttpStressTestFramework.HttpMethod.POST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpStressTestFrameworkTest {
    @Test
    void validatesConfig() {
        assertThrows(IllegalArgumentException.class, () ->
                HttpStressTestFramework.StressTestConfig.builder().totalRequests(0).url("http://localhost").build());
        assertThrows(IllegalArgumentException.class, () ->
                HttpStressTestFramework.StressTestConfig.builder().threadPoolSize(0).url("http://localhost").build());
        assertThrows(IllegalArgumentException.class, () ->
                HttpStressTestFramework.StressTestConfig.builder().url(" ").build());
        assertThrows(IllegalArgumentException.class, () ->
                HttpStressTestFramework.StressTestConfig.builder().url("http://localhost").connectTimeoutMs(-1).build());
    }

    @Test
    void aggregatesMetricsAndPercentiles() {
        HttpStressTestFramework.StressTestConfig config = HttpStressTestFramework.StressTestConfig.builder()
                .url("http://localhost")
                .totalRequests(5)
                .build();
        HttpStressTestFramework.MetricsCollector collector = new HttpStressTestFramework.MetricsCollector();

        collector.record(HttpStressTestFramework.RequestResult.completed(200, 10));
        collector.record(HttpStressTestFramework.RequestResult.completed(204, 20));
        collector.record(HttpStressTestFramework.RequestResult.completed(404, 30));
        collector.record(HttpStressTestFramework.RequestResult.failure(-1, 40, "boom"));
        collector.record(HttpStressTestFramework.RequestResult.completed(500, 50));

        HttpStressTestFramework.StressTestReport report = collector.report(config, 1000);

        assertEquals(5, report.getTotalRequests());
        assertEquals(2, report.getSuccessfulRequests());
        assertEquals(3, report.getFailedRequests());
        assertEquals(40.0, report.getSuccessRate());
        assertEquals(10, report.getMinLatencyMs());
        assertEquals(50, report.getMaxLatencyMs());
        assertEquals(30.0, report.getAverageLatencyMs());
        assertEquals(30, report.getP50LatencyMs());
        assertEquals(50, report.getP95LatencyMs());
        assertEquals(5.0, report.getRequestsPerSecond());
        assertEquals(Integer.valueOf(1), report.getStatusCodeCounts().get(200));
        assertEquals(Integer.valueOf(1), report.getStatusCodeCounts().get(-1));
    }

    @Test
    void runnerContinuesWhenIndividualRequestsFail() {
        HttpStressTestFramework.RequestExecutor executor = new HttpStressTestFramework.RequestExecutor() {
            private final AtomicInteger calls = new AtomicInteger();

            public HttpStressTestFramework.RequestResult execute(HttpStressTestFramework.StressTestConfig config) {
                int call = calls.incrementAndGet();
                if (call % 2 == 0) {
                    return HttpStressTestFramework.RequestResult.failure(-1, 5, "failure");
                }
                return HttpStressTestFramework.RequestResult.completed(200, 3);
            }
        };
        HttpStressTestFramework.StressTestConfig config = HttpStressTestFramework.StressTestConfig.builder()
                .url("http://localhost")
                .totalRequests(6)
                .threadPoolSize(3)
                .build();

        HttpStressTestFramework.StressTestReport report = new HttpStressTestFramework.StressTestRunner(executor).run(config);

        assertEquals(6, report.getTotalRequests());
        assertEquals(3, report.getSuccessfulRequests());
        assertEquals(3, report.getFailedRequests());
    }

    @Test
    void executesConcurrentGetRequestsAgainstLocalHttpServer() throws Exception {
        HttpServer server = startServer(200, "OK");
        try {
            String url = "http://localhost:" + server.getAddress().getPort() + "/health";
            HttpStressTestFramework.StressTestConfig config = HttpStressTestFramework.StressTestConfig.builder()
                    .url(url)
                    .method(GET)
                    .totalRequests(20)
                    .threadPoolSize(5)
                    .connectTimeoutMs(1000)
                    .readTimeoutMs(1000)
                    .build();

            HttpStressTestFramework.StressTestReport report = HttpStressTestFramework.run(config);

            assertEquals(20, report.getTotalRequests());
            assertEquals(20, report.getSuccessfulRequests());
            assertEquals(0, report.getFailedRequests());
            assertEquals(Integer.valueOf(20), report.getStatusCodeCounts().get(200));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void non2xxStatusCountsAsFailureButStillReported() throws Exception {
        HttpServer server = startServer(404, "missing");
        try {
            String url = "http://localhost:" + server.getAddress().getPort() + "/missing";
            HttpStressTestFramework.StressTestConfig config = HttpStressTestFramework.StressTestConfig.builder()
                    .url(url)
                    .method(GET)
                    .totalRequests(4)
                    .threadPoolSize(2)
                    .build();

            HttpStressTestFramework.StressTestReport report = HttpStressTestFramework.run(config);

            assertEquals(0, report.getSuccessfulRequests());
            assertEquals(4, report.getFailedRequests());
            assertEquals(Integer.valueOf(4), report.getStatusCodeCounts().get(404));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void supportsPostRequestsWithBody() throws Exception {
        final AtomicInteger postCalls = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/post", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equals(exchange.getRequestMethod())) {
                    postCalls.incrementAndGet();
                    write(exchange, 201, "created");
                } else {
                    write(exchange, 405, "bad method");
                }
            }
        });
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        try {
            HttpStressTestFramework.StressTestConfig config = HttpStressTestFramework.StressTestConfig.builder()
                    .url("http://localhost:" + server.getAddress().getPort() + "/post")
                    .method(POST)
                    .body("payload")
                    .totalRequests(3)
                    .threadPoolSize(2)
                    .build();

            HttpStressTestFramework.StressTestReport report = HttpStressTestFramework.run(config);

            assertEquals(3, postCalls.get());
            assertEquals(3, report.getSuccessfulRequests());
            assertEquals(Integer.valueOf(3), report.getStatusCodeCounts().get(201));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void textReportContainsImportantMetrics() {
        HttpStressTestFramework.StressTestConfig config = HttpStressTestFramework.StressTestConfig.builder()
                .url("http://localhost")
                .method(GET)
                .totalRequests(1)
                .build();
        HttpStressTestFramework.MetricsCollector collector = new HttpStressTestFramework.MetricsCollector();
        collector.record(HttpStressTestFramework.RequestResult.completed(200, 7));

        String text = collector.report(config, 10).toTextReport();

        assertTrue(text.contains("Stress Test Report"));
        assertTrue(text.contains("URL: http://localhost"));
        assertTrue(text.contains("Total Requests: 1"));
        assertTrue(text.contains("Status Codes: {200=1}"));
    }

    private static HttpServer startServer(final int statusCode, final String body) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                write(exchange, statusCode, body);
            }
        });
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        return server;
    }

    private static void write(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream output = exchange.getResponseBody();
        output.write(bytes);
        output.close();
    }
}
