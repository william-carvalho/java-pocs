package com.example.taxsystem.stresstest.api;

import javax.validation.constraints.NotBlank;
import java.util.LinkedHashMap;
import java.util.Map;

public class StressTargetRequest {

    @NotBlank
    private String url;

    @NotBlank
    private String method;

    private Map<String, String> headers = new LinkedHashMap<String, String>();
    private String payload;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
