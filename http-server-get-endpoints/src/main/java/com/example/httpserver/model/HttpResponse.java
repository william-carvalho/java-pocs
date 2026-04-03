package com.example.httpserver.model;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private final int statusCode;
    private final String reasonPhrase;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(int statusCode, String reasonPhrase, Map<String, String> headers, byte[] body) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse okText(String body) {
        return build(200, "OK", "text/plain; charset=UTF-8", body);
    }

    public static HttpResponse okJson(String body) {
        return build(200, "OK", "application/json; charset=UTF-8", body);
    }

    public static HttpResponse badRequest(String body) {
        return build(400, "Bad Request", "text/plain; charset=UTF-8", body);
    }

    public static HttpResponse notFound(String body) {
        return build(404, "Not Found", "text/plain; charset=UTF-8", body);
    }

    public static HttpResponse methodNotAllowed(String body) {
        return build(405, "Method Not Allowed", "text/plain; charset=UTF-8", body);
    }

    public static HttpResponse internalServerError(String body) {
        return build(500, "Internal Server Error", "text/plain; charset=UTF-8", body);
    }

    private static HttpResponse build(int statusCode, String reasonPhrase, String contentType, String body) {
        byte[] responseBody = body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);
        Map<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(responseBody.length));
        headers.put("Connection", "close");
        return new HttpResponse(statusCode, reasonPhrase, headers, responseBody);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}

