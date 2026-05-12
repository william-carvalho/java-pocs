# Logger Builder Router Single Class

Java 8 POC for a logger builder/router system.

The production code is intentionally kept in one class:

```text
src/main/java/com/example/loggerrouter/LoggerBuilderRouter.java
```

## Features

- Same API for sync and async logging.
- Built-in file-system destination.
- Built-in in-memory destination suitable for ELK-style adapters and tests.
- Custom destinations through `LogSink`.
- Builder API for configuring routes.

## Example

```java
LoggerBuilderRouter logger = LoggerBuilderRouter.builder()
        .routeToFileSystem("FS", Paths.get("logs/app.log"))
        .routeToInMemory("ELK")
        .route("CUSTOM", event -> System.out.println(event.message()))
        .build();

logger.log(LoggerBuilderRouter.event()
        .destination("FS")
        .level(LoggerBuilderRouter.Level.INFO)
        .message("User created")
        .metadata("userId", "123")
        .sync()
        .build());

logger.log(LoggerBuilderRouter.event()
        .destination("ELK")
        .level(LoggerBuilderRouter.Level.ERROR)
        .message("Payment timeout")
        .async()
        .build());
```

## Test

```bash
mvn test
```
