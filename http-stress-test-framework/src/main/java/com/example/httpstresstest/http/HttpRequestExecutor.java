package com.example.httpstresstest.http;

import com.example.httpstresstest.config.HttpMethod;
import com.example.httpstresstest.config.StressTestConfig;
import com.example.httpstresstest.model.RequestResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

public class HttpRequestExecutor {

    public RequestResult execute(StressTestConfig config) {
        if (config.getDelayBetweenRequestsMs() > 0) {
            sleep(config.getDelayBetweenRequestsMs());
        }

        LocalDateTime startedAt = LocalDateTime.now();
        long start = System.nanoTime();
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(config.getUrl()).openConnection();
            connection.setRequestMethod(config.getMethod().name());
            connection.setConnectTimeout(config.getConnectTimeoutMs());
            connection.setReadTimeout(config.getReadTimeoutMs());
            connection.setUseCaches(false);

            for (Map.Entry<String, String> header : config.getHeaders().entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }

            if (config.getMethod() == HttpMethod.POST) {
                connection.setDoOutput(true);
                String body = config.getRequestBody() == null ? "" : config.getRequestBody();
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                if (!config.getHeaders().containsKey("Content-Type")) {
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                }
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(bytes);
                }
            }

            int statusCode = connection.getResponseCode();
            consumeQuietly(statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream());
            long latencyMs = elapsedMs(start);
            boolean success = statusCode >= 200 && statusCode < 300;

            return new RequestResult(success, statusCode, latencyMs, null, startedAt, LocalDateTime.now());
        } catch (Exception exception) {
            return new RequestResult(false, -1, elapsedMs(start), exception.getMessage(), startedAt, LocalDateTime.now());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void consumeQuietly(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return;
        }
        try (InputStream stream = inputStream) {
            byte[] buffer = new byte[256];
            while (stream.read(buffer) != -1) {
            }
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted during delay", exception);
        }
    }

    private long elapsedMs(long start) {
        return (System.nanoTime() - start) / 1_000_000L;
    }
}

