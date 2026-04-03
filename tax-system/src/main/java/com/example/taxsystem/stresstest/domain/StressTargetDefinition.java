package com.example.taxsystem.stresstest.domain;

import com.example.taxsystem.common.annotation.Mappable;
import org.springframework.http.HttpMethod;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;

@Mappable
public class StressTargetDefinition {

    @NotBlank
    private String url;

    @NotNull
    private HttpMethod method;

    private Map<String, String> headers = new LinkedHashMap<String, String>();
    private String payload;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
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
