# HTTP Stress Test Single Class

Java 8 POC for an HTTP stress test framework.

The production code is intentionally in one class:

```text
src/main/java/com/example/httpstress/HttpStressTestFramework.java
```

## Features

- GET and POST requests.
- Concurrent execution with a fixed thread pool.
- Per-request latency measurement.
- Success and failure counts.
- Status code aggregation.
- Min, max, average, p50, and p95 latency.
- Total duration and requests per second.
- Text report rendering.

## Example

```java
HttpStressTestFramework.StressTestConfig config =
        HttpStressTestFramework.StressTestConfig.builder()
                .url("http://localhost:8080/health")
                .method(HttpStressTestFramework.HttpMethod.GET)
                .totalRequests(100)
                .threadPoolSize(10)
                .build();

HttpStressTestFramework.StressTestReport report =
        HttpStressTestFramework.run(config);
```

## Test

```bash
mvn test
```
