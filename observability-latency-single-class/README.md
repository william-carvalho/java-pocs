# Observability Latency Single Class

Java 8 Spring Boot observability core framework based on latency metrics.

Features:

- record latency by operation
- aggregate count, average, min, max, and total latency
- observe a `Callable` and measure execution time
- list metric summaries
- expose a simple latency health check

## Run

```bash
mvn spring-boot:run
```

## Test

```bash
mvn test
```

## API

Record latency:

```bash
curl -X POST http://localhost:8080/latencies \
  -H "Content-Type: application/json" \
  -d "{\"operation\":\"checkout\",\"latencyMillis\":120}"
```

Get one summary:

```bash
curl http://localhost:8080/latencies/checkout
```

List summaries:

```bash
curl http://localhost:8080/latencies
```

Health:

```bash
curl http://localhost:8080/health/latency
```
