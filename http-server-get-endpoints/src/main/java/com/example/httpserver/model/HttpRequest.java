package com.example.httpserver.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String rawPath;
    private final String path;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;

    public HttpRequest(String method,
                       String rawPath,
                       String path,
                       String httpVersion,
                       Map<String, String> headers,
                       Map<String, String> queryParams) {
        this.method = method;
        this.rawPath = rawPath;
        this.path = path;
        this.httpVersion = httpVersion;
        this.headers = new HashMap<String, String>(headers);
        this.queryParams = new HashMap<String, String>(queryParams);
    }

    public String getMethod() {
        return method;
    }

    public String getRawPath() {
        return rawPath;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public Map<String, String> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    public String getQueryParam(String name) {
        return queryParams.get(name);
    }
}

