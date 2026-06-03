# Task Framework Single Class

Java 8 Spring Boot task framework with one production class.

Features:

- submit tasks
- run tasks on an internal pool of worker threads
- inspect task status
- list submitted tasks
- grow the worker pool

## Run

```bash
mvn spring-boot:run
```

## Test

```bash
mvn test
```

## API

Submit a task:

```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"send-email\",\"durationMillis\":500}"
```

Get a task:

```bash
curl http://localhost:8080/tasks/{id}
```

List tasks:

```bash
curl http://localhost:8080/tasks
```

Grow the pool:

```bash
curl -X POST "http://localhost:8080/pool/resize?size=5"
```
