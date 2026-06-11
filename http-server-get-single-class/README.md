# HTTP Server GET Single Class

Java 8 POC for a simple HTTP server supporting GET endpoints.

The production code is intentionally in one class:

```text
src/main/java/com/example/httpget/HttpGetServer.java
```

## Endpoints

- `GET /` returns `HTTP Server is running`
- `GET /health` returns `{"status":"UP"}`
- `GET /hello` returns `Hello, World`
- `GET /hello?name=Ana` returns `Hello, Ana`
- `GET /time` returns the current server time
- `GET /echo?foo=bar` returns query params as JSON

## Rules

- Only `GET` is supported.
- Other methods return `405 Method Not Allowed`.
- Unknown paths return `404 Not Found`.
- Malformed requests return `400 Bad Request`.
- Responses include status line, headers, and body.

## Example

```java
HttpGetServer server = new HttpGetServer();
String response = server.handleToHttp("GET /health HTTP/1.1\r\nHost: localhost\r\n\r\n");
```

## Test

```bash
mvn test
```
