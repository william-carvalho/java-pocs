package com.example.httpstresstest.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class StressTestConfig {

    private final String url;
    private final HttpMethod method;
    private final int totalRequests;
    private final int threadPoolSize;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;
    private final Map<String, String> headers;
    private final String requestBody;
    private final long delayBetweenRequestsMs;

    private StressTestConfig(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.totalRequests = builder.totalRequests;
        this.threadPoolSize = builder.threadPoolSize;
        this.connectTimeoutMs = builder.connectTimeoutMs;
        this.readTimeoutMs = builder.readTimeoutMs;
        this.headers = new LinkedHashMap<String, String>(builder.headers);
        this.requestBody = builder.requestBody;
        this.delayBetweenRequestsMs = builder.delayBetweenRequestsMs;
    }

    public static Builder builder() {
        return new Builder();
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

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public String getRequestBody() {
        return requestBody;
    }

    public long getDelayBetweenRequestsMs() {
        return delayBetweenRequestsMs;
    }

    public static class Builder {

        private String url;
        private HttpMethod method;
        private int totalRequests;
        private int threadPoolSize;
        private int connectTimeoutMs = 3000;
        private int readTimeoutMs = 3000;
        private Map<String, String> headers = new LinkedHashMap<String, String>();
        private String requestBody;
        private long delayBetweenRequestsMs;

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

        public Builder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers == null ? new LinkedHashMap<String, String>() : new LinkedHashMap<String, String>(headers);
            return this;
        }

        public Builder requestBody(String requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public Builder delayBetweenRequestsMs(long delayBetweenRequestsMs) {
            this.delayBetweenRequestsMs = delayBetweenRequestsMs;
            return this;
        }

        public StressTestConfig build() {
            return new StressTestConfig(this);
        }
    }
}

