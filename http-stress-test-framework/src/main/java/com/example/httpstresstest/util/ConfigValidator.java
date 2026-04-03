package com.example.httpstresstest.util;

import com.example.httpstresstest.config.HttpMethod;
import com.example.httpstresstest.config.StressTestConfig;
import com.example.httpstresstest.exception.StressTestException;

import java.net.MalformedURLException;
import java.net.URL;

public final class ConfigValidator {

    private ConfigValidator() {
    }

    public static void validate(StressTestConfig config) {
        if (config == null) {
            throw new StressTestException("Stress test config must not be null");
        }
        requireUrl(config.getUrl());
        requireMethod(config.getMethod());
        if (config.getTotalRequests() <= 0) {
            throw new StressTestException("totalRequests must be greater than zero");
        }
        if (config.getThreadPoolSize() <= 0) {
            throw new StressTestException("threadPoolSize must be greater than zero");
        }
        if (config.getConnectTimeoutMs() < 0 || config.getReadTimeoutMs() < 0) {
            throw new StressTestException("Timeout values must be greater than or equal to zero");
        }
        if (config.getDelayBetweenRequestsMs() < 0) {
            throw new StressTestException("delayBetweenRequestsMs must be greater than or equal to zero");
        }
        if (config.getMethod() == HttpMethod.GET && config.getRequestBody() != null && !config.getRequestBody().trim().isEmpty()) {
            throw new StressTestException("GET requests must not define requestBody in this POC");
        }
    }

    private static void requireUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new StressTestException("url is required");
        }
        try {
            new URL(url);
        } catch (MalformedURLException exception) {
            throw new StressTestException("Invalid URL: " + url, exception);
        }
    }

    private static void requireMethod(HttpMethod method) {
        if (method == null) {
            throw new StressTestException("method is required");
        }
    }
}

