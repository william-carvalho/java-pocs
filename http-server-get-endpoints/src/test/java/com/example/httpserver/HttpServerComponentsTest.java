package com.example.httpserver;

import com.example.httpserver.http.HttpRequestParser;
import com.example.httpserver.http.HttpResponseWriter;
import com.example.httpserver.model.HttpRequest;
import com.example.httpserver.model.HttpResponse;
import com.example.httpserver.router.HttpRouter;
import com.example.httpserver.util.QueryParamParser;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpServerComponentsTest {

    @Test
    void shouldParseMethodPathAndQueryParams() throws Exception {
        String rawRequest = "GET /hello?name=William&city=Floripa HTTP/1.1\r\nHost: localhost\r\n\r\n";
        HttpRequestParser parser = new HttpRequestParser();

        HttpRequest request = parser.parse(new BufferedReader(new StringReader(rawRequest)));

        assertEquals("GET", request.getMethod());
        assertEquals("/hello", request.getPath());
        assertEquals("/hello?name=William&city=Floripa", request.getRawPath());
        assertEquals("William", request.getQueryParam("name"));
        assertEquals("Floripa", request.getQueryParam("city"));
        assertEquals("localhost", request.getHeaders().get("Host"));
    }

    @Test
    void shouldRouteToCorrectGetHandler() {
        HttpRouter router = new HttpRouter();
        router.get("/health", request -> HttpResponse.okJson("{\"status\":\"UP\"}"));

        HttpRequest request = new HttpRequest("GET", "/health", "/health", "HTTP/1.1",
                java.util.Collections.<String, String>emptyMap(),
                java.util.Collections.<String, String>emptyMap());

        HttpResponse response = router.route(request);

        assertEquals(200, response.getStatusCode());
        assertEquals("{\"status\":\"UP\"}", new String(response.getBody(), StandardCharsets.UTF_8));
    }

    @Test
    void shouldReturnNotFoundForUnknownPath() {
        HttpRouter router = new HttpRouter();
        HttpRequest request = new HttpRequest("GET", "/unknown", "/unknown", "HTTP/1.1",
                java.util.Collections.<String, String>emptyMap(),
                java.util.Collections.<String, String>emptyMap());

        HttpResponse response = router.route(request);

        assertEquals(404, response.getStatusCode());
    }

    @Test
    void shouldReturnMethodNotAllowedForInvalidMethod() {
        HttpRouter router = new HttpRouter();
        router.get("/", request -> HttpResponse.okText("ok"));
        HttpRequest request = new HttpRequest("POST", "/", "/", "HTTP/1.1",
                java.util.Collections.<String, String>emptyMap(),
                java.util.Collections.<String, String>emptyMap());

        HttpResponse response = router.route(request);

        assertEquals(405, response.getStatusCode());
    }

    @Test
    void shouldBuildValidHttpResponseText() throws Exception {
        HttpResponse response = HttpResponse.okText("HTTP Server is running");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        new HttpResponseWriter().write(outputStream, response);

        String rawResponse = outputStream.toString("UTF-8");
        assertTrue(rawResponse.startsWith("HTTP/1.1 200 OK"));
        assertTrue(rawResponse.contains("Content-Type: text/plain; charset=UTF-8"));
        assertTrue(rawResponse.contains("Connection: close"));
        assertTrue(rawResponse.endsWith("HTTP Server is running"));
    }

    @Test
    void shouldParseQueryParamsCorrectly() {
        Map<String, String> params = QueryParamParser.parse("foo=bar&x=1");

        assertEquals("bar", params.get("foo"));
        assertEquals("1", params.get("x"));
    }
}

