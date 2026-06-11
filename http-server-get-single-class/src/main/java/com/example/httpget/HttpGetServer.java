package com.example.httpget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class HttpGetServer {
    private final Router router;

    public HttpGetServer() {
        this(defaultRouter());
    }

    public HttpGetServer(Router router) {
        if (router == null) {
            throw new IllegalArgumentException("router is required");
        }
        this.router = router;
    }

    public Response handle(String rawRequest) {
        try {
            Request request = Request.parse(rawRequest);
            if (!"GET".equals(request.getMethod())) {
                return Response.text(405, "Method Not Allowed", "Method Not Allowed")
                        .header("Allow", "GET");
            }
            return router.route(request);
        } catch (BadRequestException ex) {
            return Response.text(400, "Bad Request", ex.getMessage());
        } catch (Exception ex) {
            return Response.text(500, "Internal Server Error", "Internal Server Error");
        }
    }

    public String handleToHttp(String rawRequest) {
        return handle(rawRequest).toHttp();
    }

    public RunningServer start(int port) throws IOException {
        return new RunningServer(port, this);
    }

    public static Router defaultRouter() {
        Router router = new Router();
        router.get("/", new Handler() {
            public Response handle(Request request) {
                return Response.text(200, "OK", "HTTP Server is running");
            }
        });
        router.get("/health", new Handler() {
            public Response handle(Request request) {
                return Response.json(200, "OK", "{\"status\":\"UP\"}");
            }
        });
        router.get("/hello", new Handler() {
            public Response handle(Request request) {
                String name = request.queryParam("name");
                return Response.text(200, "OK", "Hello, " + (name == null ? "World" : name));
            }
        });
        router.get("/time", new Handler() {
            public Response handle(Request request) {
                return Response.text(200, "OK", LocalDateTime.now().toString());
            }
        });
        router.get("/echo", new Handler() {
            public Response handle(Request request) {
                return Response.json(200, "OK", toJson(request.getQueryParams()));
            }
        });
        return router;
    }

    private static String toJson(Map<String, String> values) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(escapeJson(entry.getKey())).append("\":\"")
                    .append(escapeJson(entry.getValue())).append("\"");
            first = false;
        }
        json.append("}");
        return json.toString();
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public interface Handler {
        Response handle(Request request);
    }

    public static final class Router {
        private final Map<String, Handler> getRoutes = new LinkedHashMap<String, Handler>();

        public Router get(String path, Handler handler) {
            if (isBlank(path) || !path.startsWith("/")) {
                throw new IllegalArgumentException("path must start with /");
            }
            if (handler == null) {
                throw new IllegalArgumentException("handler is required");
            }
            getRoutes.put(path, handler);
            return this;
        }

        private Response route(Request request) {
            Handler handler = getRoutes.get(request.getPath());
            if (handler == null) {
                return Response.text(404, "Not Found", "Not Found");
            }
            return handler.handle(request);
        }
    }

    public static final class Request {
        private final String method;
        private final String path;
        private final String version;
        private final Map<String, String> queryParams;
        private final Map<String, String> headers;

        private Request(String method, String path, String version, Map<String, String> queryParams, Map<String, String> headers) {
            this.method = method;
            this.path = path;
            this.version = version;
            this.queryParams = Collections.unmodifiableMap(new LinkedHashMap<String, String>(queryParams));
            this.headers = Collections.unmodifiableMap(new LinkedHashMap<String, String>(headers));
        }

        public static Request parse(String rawRequest) {
            if (isBlank(rawRequest)) {
                throw new BadRequestException("Empty request");
            }

            String[] lines = rawRequest.split("\\r?\\n");
            String[] requestLine = lines[0].trim().split("\\s+");
            if (requestLine.length != 3 || !requestLine[2].startsWith("HTTP/")) {
                throw new BadRequestException("Malformed request line");
            }

            String target = requestLine[1];
            String path = target;
            String query = "";
            int queryStart = target.indexOf('?');
            if (queryStart >= 0) {
                path = target.substring(0, queryStart);
                query = target.substring(queryStart + 1);
            }
            if (isBlank(path) || !path.startsWith("/")) {
                throw new BadRequestException("Malformed path");
            }

            Map<String, String> headers = new LinkedHashMap<String, String>();
            for (int index = 1; index < lines.length; index++) {
                String line = lines[index];
                if (line.trim().isEmpty()) {
                    break;
                }
                int separator = line.indexOf(':');
                if (separator <= 0) {
                    throw new BadRequestException("Malformed header");
                }
                headers.put(line.substring(0, separator).trim(), line.substring(separator + 1).trim());
            }

            return new Request(requestLine[0], path, requestLine[2], parseQuery(query), headers);
        }

        private static Map<String, String> parseQuery(String query) {
            Map<String, String> params = new LinkedHashMap<String, String>();
            if (isBlank(query)) {
                return params;
            }
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                if (pair.isEmpty()) {
                    continue;
                }
                int separator = pair.indexOf('=');
                String key = separator >= 0 ? pair.substring(0, separator) : pair;
                String value = separator >= 0 ? pair.substring(separator + 1) : "";
                params.put(decode(key), decode(value));
            }
            return params;
        }

        private static String decode(String value) {
            try {
                return URLDecoder.decode(value, "UTF-8");
            } catch (Exception ex) {
                throw new BadRequestException("Malformed query string");
            }
        }

        public String getMethod() {
            return method;
        }

        public String getPath() {
            return path;
        }

        public String getVersion() {
            return version;
        }

        public Map<String, String> getQueryParams() {
            return queryParams;
        }

        public String queryParam(String name) {
            return queryParams.get(name);
        }

        public Map<String, String> getHeaders() {
            return headers;
        }
    }

    public static final class Response {
        private final int statusCode;
        private final String reasonPhrase;
        private final Map<String, String> headers = new LinkedHashMap<String, String>();
        private final String body;

        private Response(int statusCode, String reasonPhrase, String body) {
            this.statusCode = statusCode;
            this.reasonPhrase = reasonPhrase;
            this.body = body == null ? "" : body;
            header("Content-Length", String.valueOf(this.body.getBytes(StandardCharsets.UTF_8).length));
            header("Connection", "close");
        }

        public static Response text(int statusCode, String reasonPhrase, String body) {
            return new Response(statusCode, reasonPhrase, body).header("Content-Type", "text/plain; charset=utf-8");
        }

        public static Response json(int statusCode, String reasonPhrase, String body) {
            return new Response(statusCode, reasonPhrase, body).header("Content-Type", "application/json; charset=utf-8");
        }

        public Response header(String name, String value) {
            headers.put(name, value);
            return this;
        }

        public String toHttp() {
            StringBuilder response = new StringBuilder();
            response.append("HTTP/1.1 ").append(statusCode).append(" ").append(reasonPhrase).append("\r\n");
            for (Map.Entry<String, String> header : headers.entrySet()) {
                response.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
            }
            response.append("\r\n").append(body);
            return response.toString();
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getReasonPhrase() {
            return reasonPhrase;
        }

        public Map<String, String> getHeaders() {
            return Collections.unmodifiableMap(headers);
        }

        public String getBody() {
            return body;
        }
    }

    public static final class RunningServer implements AutoCloseable {
        private final ServerSocket serverSocket;
        private final ExecutorService executor = Executors.newCachedThreadPool();
        private volatile boolean running = true;

        private RunningServer(int port, final HttpGetServer server) throws IOException {
            this.serverSocket = new ServerSocket(port);
            executor.submit(new Runnable() {
                public void run() {
                    acceptLoop(server);
                }
            });
        }

        private void acceptLoop(HttpGetServer server) {
            while (running) {
                try {
                    final Socket socket = serverSocket.accept();
                    executor.submit(new Runnable() {
                        public void run() {
                            handleSocket(server, socket);
                        }
                    });
                } catch (IOException ex) {
                    if (running) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        }

        private void handleSocket(HttpGetServer server, Socket socket) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder raw = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    raw.append(line).append("\r\n");
                    if (line.isEmpty()) {
                        break;
                    }
                }
                OutputStream output = socket.getOutputStream();
                output.write(server.handleToHttp(raw.toString()).getBytes(StandardCharsets.UTF_8));
                output.flush();
            } catch (IOException ignored) {
                // A tiny POC server closes each request; failed client writes are ignored.
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }

        public int getPort() {
            return serverSocket.getLocalPort();
        }

        public void close() throws IOException {
            running = false;
            serverSocket.close();
            executor.shutdownNow();
        }
    }

    public static final class BadRequestException extends RuntimeException {
        private BadRequestException(String message) {
            super(message);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
