# Logger Router System Single Class

Java 8 POC for routing logs to FS, ELK, or custom log systems using the same API for sync and async modes.

The production code is intentionally in one class:

```text
src/main/java/com/example/loggerrouter/LoggerRouterSystem.java
```

## Features

- Builder API for configuring destinations.
- Same `log(...)` API for `SYNC` and `ASYNC`.
- File-system sink.
- In-memory ELK-style sink with structured JSON payloads.
- Custom sinks through `LogSink`.
- Metadata support.
- Async queue with `awaitAsync()` for tests and controlled shutdown.

## Example

```java
LoggerRouterSystem logger = LoggerRouterSystem.builder()
        .routeToFileSystem("FS", Paths.get("logs/app.log"))
        .routeToElk("ELK")
        .route("CUSTOM", event -> System.out.println(event.formatLine()))
        .build();

logger.log(LoggerRouterSystem.event()
        .destination("FS")
        .level(LoggerRouterSystem.LogLevel.INFO)
        .message("User created")
        .metadata("userId", "123")
        .sync()
        .build());

logger.log(LoggerRouterSystem.event()
        .destination("ELK")
        .level(LoggerRouterSystem.LogLevel.ERROR)
        .message("Payment timeout")
        .async()
        .build());
```

## Test

```bash
mvn test
```
