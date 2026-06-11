package com.example.httpget;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpGetServerTest {
    @Test
    void parsesGetRequestWithPathQueryAndHeaders() {
        HttpGetServer.Request request = HttpGetServer.Request.parse(
                "GET /hello?name=William&city=Sao%20Paulo HTTP/1.1\r\nHost: localhost\r\nAccept: text/plain\r\n\r\n");

        assertEquals("GET", request.getMethod());
        assertEquals("/hello", request.getPath());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals("William", request.queryParam("name"));
        assertEquals("Sao Paulo", request.queryParam("city"));
        assertEquals("localhost", request.getHeaders().get("Host"));
    }

    @Test
    void rootEndpointReturnsRunningMessage() {
        HttpGetServer server = new HttpGetServer();

        HttpGetServer.Response response = server.handle("GET / HTTP/1.1\r\nHost: local\r\n\r\n");

        assertEquals(200, response.getStatusCode());
        assertEquals("HTTP Server is running", response.getBody());
    }

    @Test
    void healthEndpointReturnsJson() {
        HttpGetServer server = new HttpGetServer();

        HttpGetServer.Response response = server.handle("GET /health HTTP/1.1\r\nHost: local\r\n\r\n");

        assertEquals(200, response.getStatusCode());
        assertEquals("{\"status\":\"UP\"}", response.getBody());
        assertEquals("application/json; charset=utf-8", response.getHeaders().get("Content-Type"));
    }

    @Test
    void helloEndpointUsesNameQueryParam() {
        HttpGetServer server = new HttpGetServer();

        assertEquals("Hello, World", server.handle("GET /hello HTTP/1.1\r\nHost: local\r\n\r\n").getBody());
        assertEquals("Hello, Ana", server.handle("GET /hello?name=Ana HTTP/1.1\r\nHost: local\r\n\r\n").getBody());
    }

    @Test
    void echoEndpointReturnsQueryParamsAsJson() {
        HttpGetServer server = new HttpGetServer();

        HttpGetServer.Response response = server.handle("GET /echo?foo=bar&x=1 HTTP/1.1\r\nHost: local\r\n\r\n");

        assertEquals(200, response.getStatusCode());
        assertEquals("{\"foo\":\"bar\",\"x\":\"1\"}", response.getBody());
    }

    @Test
    void unknownPathReturnsNotFound() {
        HttpGetServer server = new HttpGetServer();

        HttpGetServer.Response response = server.handle("GET /missing HTTP/1.1\r\nHost: local\r\n\r\n");

        assertEquals(404, response.getStatusCode());
        assertEquals("Not Found", response.getBody());
    }

    @Test
    void nonGetMethodReturnsMethodNotAllowed() {
        HttpGetServer server = new HttpGetServer();

        HttpGetServer.Response response = server.handle("POST /hello HTTP/1.1\r\nHost: local\r\n\r\n");

        assertEquals(405, response.getStatusCode());
        assertEquals("GET", response.getHeaders().get("Allow"));
    }

    @Test
    void malformedRequestReturnsBadRequest() {
        HttpGetServer server = new HttpGetServer();

        HttpGetServer.Response response = server.handle("BAD REQUEST\r\n\r\n");

        assertEquals(400, response.getStatusCode());
        assertEquals("Malformed request line", response.getBody());
    }

    @Test
    void responseRendersValidHttpMessage() {
        HttpGetServer.Response response = HttpGetServer.Response.text(200, "OK", "Hello");

        String raw = response.toHttp();

        assertTrue(raw.startsWith("HTTP/1.1 200 OK\r\n"));
        assertTrue(raw.contains("Content-Type: text/plain; charset=utf-8\r\n"));
        assertTrue(raw.contains("Content-Length: 5\r\n"));
        assertTrue(raw.endsWith("\r\n\r\nHello"));
    }

    @Test
    void customGetRouteCanBeRegistered() {
        HttpGetServer.Router router = new HttpGetServer.Router()
                .get("/custom", new HttpGetServer.Handler() {
                    public HttpGetServer.Response handle(HttpGetServer.Request request) {
                        return HttpGetServer.Response.text(200, "OK", "custom");
                    }
                });
        HttpGetServer server = new HttpGetServer(router);

        assertEquals("custom", server.handle("GET /custom HTTP/1.1\r\nHost: local\r\n\r\n").getBody());
    }

    @Test
    void rejectsInvalidRoutePath() {
        HttpGetServer.Router router = new HttpGetServer.Router();

        assertThrows(IllegalArgumentException.class, () ->
                router.get("missing-slash", new HttpGetServer.Handler() {
                    public HttpGetServer.Response handle(HttpGetServer.Request request) {
                        return HttpGetServer.Response.text(200, "OK", "x");
                    }
                }));
    }

    @Test
    void canServeRequestThroughSocket() throws Exception {
        HttpGetServer server = new HttpGetServer();
        HttpGetServer.RunningServer runningServer = server.start(0);
        try {
            Socket socket = new Socket("localhost", runningServer.getPort());
            OutputStream output = socket.getOutputStream();
            output.write("GET /health HTTP/1.1\r\nHost: localhost\r\n\r\n".getBytes(StandardCharsets.UTF_8));
            output.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String statusLine = reader.readLine();
            String line;
            String body = null;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    body = reader.readLine();
                    break;
                }
            }
            socket.close();

            assertEquals("HTTP/1.1 200 OK", statusLine);
            assertEquals("{\"status\":\"UP\"}", body);
        } finally {
            runningServer.close();
        }
    }
}
