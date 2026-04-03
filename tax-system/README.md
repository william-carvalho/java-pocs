# Tax System

Compact Java 8 + Spring Boot showcase built as a single Maven application with three focused modules:

- Grocery TODO List
- Generic Converter Framework
- HTTP Stress Test Framework

The repository folder is named `tax-system` because that was the requested project root, but the actual implementation intentionally demonstrates three different backend concerns in one cohesive codebase.

## Overview

This project is a portfolio-ready reference implementation for pragmatic backend engineering:

- Java 8 source/target compatibility
- Spring Boot 2.7.x for a modern-enough but Java 8 friendly baseline
- layered architecture without turning the project into a heavy enterprise template
- real persistence, security, validation, caching, conversion, concurrency, and tests
- clean separation between API, application, domain, and infrastructure

The goal is not to mimic a full production platform or a JMeter clone. The goal is to show judgment.

## Architecture

The project uses a compact monorepo-style package layout:

```text
com.example.taxsystem
├── common
│   ├── annotation
│   ├── config
│   ├── exception
│   ├── reflection
│   ├── util
│   └── validation
├── converter
│   ├── api
│   ├── application
│   ├── domain
│   └── infrastructure
├── grocery
│   ├── api
│   ├── application
│   ├── domain
│   └── infrastructure
└── stresstest
    ├── api
    ├── application
    ├── domain
    └── infrastructure
```

### Layering

- `api`: REST contracts and controllers
- `application`: orchestration, use cases, transaction boundaries
- `domain`: business models and domain-level concepts
- `infrastructure`: JPA, HTTP client integration, converter implementations, runtime concerns
- `common`: shared support code such as configuration, annotations, reflection, validation, and error handling

## Technology Choices

### Included

- Spring Boot `2.7.18`
- Spring Web
- Spring Security
- Spring Data JPA
- H2
- Bean Validation
- Spring Cache + Caffeine
- Gson
- Guava
- Resilience4j
- JUnit 5
- Mockito
- Spring Boot Test

### Intentionally Excluded

- Quartz: not needed for the current stress runner or demo flows
- Apache Camel: too heavy for a compact showcase
- gRPC: adds surface area without improving these use cases
- Vert.x: not necessary for the current concurrency and load-test goals
- Quarkus: would duplicate the role already covered by Spring Boot here
- Joda-Time: Java Time is preferred on Java 8 and avoids carrying legacy date APIs

## Key Features

### 1. Grocery TODO List

Supports:

- create item
- remove item
- mark as done
- do
- redo
- list all
- list by status
- optional name search
- combined `status` + `name` filtering

Implementation notes:

- persisted with Spring Data JPA + H2
- write operations protected by Spring Security
- read-heavy `listAll` cached with Caffeine
- persistence entity is not exposed directly to the API
- entity -> domain mapping uses the converter framework

### 2. Converter Framework

Provides:

- `Converter<S, T>` abstraction
- `ConverterRegistry`
- `ConverterEngine`
- custom converter registration
- nested object conversion
- collection conversion
- reflection-based field mapping fallback
- annotation-driven behavior with:
  - `@Mappable`
  - `@IgnoreField`
  - `@FieldAlias`
  - `@UseConverter`

Supported examples:

- `GroceryItemEntity -> GroceryItem`
- `GroceryItem -> GroceryItemResponse`
- `Map<String, Object> -> StressTestConfig`
- `String -> LocalDateTime`

### 3. HTTP Stress Test Framework

Supports:

- configurable target URL
- HTTP method
- headers
- payload
- total requests
- concurrency level
- timeout
- optional ramp-up
- sync execution
- async execution with in-memory execution status tracking
- retry on outbound HTTP using Resilience4j
- result aggregation with:
  - success and error counters
  - average, min, max latency
  - p50 and p95 latency
  - throughput estimate
- console summary
- JSON response/report

Implementation notes:

- `ExecutorService`
- `Callable`
- `Future`
- atomic counters
- `ReentrantLock` around latency/error sampling
- prototype-scoped `StressExecutionContext` per run

## Spring Notes

### Dependency Injection

- constructor injection is the default everywhere
- Spring singletons are used naturally for services, registries, repositories, and config beans
- prototype scope is used for `StressExecutionContext`, because each stress execution should have a fresh runtime identity

### BeanPostProcessor

`ModuleLoggingBeanPostProcessor` logs initialization of showcase beans from the three main modules. It is intentionally small, but it demonstrates a real extension point instead of a toy example.

### Security

Read endpoints are open where it makes sense:

- `GET /api/grocery/**`
- `GET /api/stress-tests/reports/**`

Write endpoints require `ROLE_WRITER`.

Current credentials:

- `reader / reader-secret`
- `writer / writer-secret`

CSRF is disabled because this showcase uses HTTP Basic for stateless API calls. In a browser-oriented app, that decision would need to be revisited.

### Spring Security Compatibility

This project uses:

- Spring Boot `2.7.x`
- Spring Security `5.7.x`
- Java 8 compatible APIs

That is why the configuration uses `SecurityFilterChain` but still relies on the `authorizeRequests` / `antMatchers` style rather than newer Spring Security 6 APIs that would move the project away from the requested Java 8 compatible baseline.

## Java 8 / Core Java Usage

The code intentionally uses Java 8 features where they fit:

- Streams for filtering, mapping, sorting, and aggregation
- functional interfaces such as `Predicate`, `Supplier`, `UnaryOperator`, and `BinaryOperator`
- Java Time API for timestamps and scheduling metadata
- Reflection API inside the converter engine
- `Properties` API through `PropertyFileLoader`
- concurrency primitives in the stress module
- custom annotations for mapping customization

## Design Patterns Used

- Strategy: converter selection is driven by registered `Converter` implementations
- Factory: `StressReportFactory` centralizes report construction
- Builder: `StressTestReport.Builder` keeps report creation explicit and readable
- Adapter: `LocalDateTimeGsonAdapter` bridges Java Time and Gson
- Singleton via Spring: default lifecycle for services/registries/configuration beans

Patterns were only used where they reduced coupling or improved readability. The code intentionally does not try to force every GoF pattern into the project.

## Running Locally

### Prerequisites

- Java 8 or newer installed
- Maven 3.8+

### Run

```bash
mvn spring-boot:run
```

Application defaults:

- base URL: `http://localhost:8080`
- H2 datasource: in-memory

### Run Tests

```bash
mvn test
```

## Configuration

Main config:

- `src/main/resources/application.yml`

Sample properties file:

- `src/main/resources/sample-taxsystem.properties`

Test profile:

- `src/main/resources/application-test.yml`

The sample properties file is intentionally simple and is exposed through the converter sample endpoint to demonstrate classic `.properties` loading in addition to Spring configuration properties.

## API Examples

### Grocery

Create item:

```bash
curl -u writer:writer-secret \
  -X POST http://localhost:8080/api/grocery/items \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"oat milk\"}"
```

Sample response:

```json
{
  "id": 3,
  "name": "oat milk",
  "status": "PENDING",
  "audit": {
    "createdAt": "2026-04-03T10:00:00",
    "updatedAt": "2026-04-03T10:00:00",
    "completedAt": null
  }
}
```

List all:

```bash
curl http://localhost:8080/api/grocery/items
```

Filter by status:

```bash
curl "http://localhost:8080/api/grocery/items?status=DONE"
```

Filter by status and name:

```bash
curl "http://localhost:8080/api/grocery/items?status=PENDING&name=milk"
```

Mark as done:

```bash
curl -u writer:writer-secret \
  -X PUT http://localhost:8080/api/grocery/items/1/done
```

Redo:

```bash
curl -u writer:writer-secret \
  -X PUT http://localhost:8080/api/grocery/items/1/redo
```

Remove:

```bash
curl -u writer:writer-secret \
  -X DELETE http://localhost:8080/api/grocery/items/1
```

### Converter

Preview `Map -> StressTestConfig` conversion:

```bash
curl -u writer:writer-secret \
  -X POST http://localhost:8080/api/converter/preview/stress-config \
  -H "Content-Type: application/json" \
  -d '{
    "target": {
      "url": "https://httpbin.org/post",
      "method": "POST",
      "headers": {
        "Content-Type": "application/json"
      },
      "payload": "{\"hello\":\"world\"}"
    },
    "execution": {
      "totalRequests": 10,
      "concurrencyLevel": 2,
      "timeoutMillis": 1000,
      "rampUpMillis": 100,
      "mode": "ASYNC",
      "scheduledAt": "2026-04-03T12:30:00"
    }
  }'
```

Read sample properties:

```bash
curl -u reader:reader-secret \
  http://localhost:8080/api/converter/samples/properties
```

### Stress Test

Run synchronously:

```bash
curl -u writer:writer-secret \
  -X POST http://localhost:8080/api/stress-tests \
  -H "Content-Type: application/json" \
  -d '{
    "target": {
      "url": "https://httpbin.org/get",
      "method": "GET",
      "headers": {
        "Accept": "application/json"
      }
    },
    "execution": {
      "totalRequests": 20,
      "concurrencyLevel": 4,
      "timeoutMillis": 1500,
      "rampUpMillis": 200,
      "mode": "SYNC",
      "scheduledAt": "2026-04-03T14:00:00"
    }
  }'
```

Sample response shape:

```json
{
  "executionId": "2d2dd2d6-5d8c-4e94-b1f2-2b3d8f01dc45",
  "url": "https://httpbin.org/get",
  "method": "GET",
  "mode": "SYNC",
  "totalRequests": 20,
  "successCount": 20,
  "errorCount": 0,
  "averageLatencyMillis": 42,
  "minLatencyMillis": 18,
  "maxLatencyMillis": 89,
  "p50LatencyMillis": 39,
  "p95LatencyMillis": 84,
  "throughputPerSecond": 61.72,
  "durationMillis": 324,
  "startedAt": "2026-04-03T14:00:00",
  "completedAt": "2026-04-03T14:00:00",
  "sampleErrors": []
}
```

Run asynchronously:

```bash
curl -u writer:writer-secret \
  -X POST http://localhost:8080/api/stress-tests/async \
  -H "Content-Type: application/json" \
  -d '{
    "target": {
      "url": "https://httpbin.org/status/200",
      "method": "GET"
    },
    "execution": {
      "totalRequests": 50,
      "concurrencyLevel": 8,
      "timeoutMillis": 1000,
      "rampUpMillis": 300,
      "mode": "ASYNC",
      "scheduledAt": "2026-04-03T15:00:00"
    }
  }'
```

Fetch async report:

```bash
curl http://localhost:8080/api/stress-tests/reports/{executionId}
```

## Testing

The test suite covers the critical paths without bloating the project:

- converter engine unit tests
- grocery service unit tests
- grocery controller tests
- grocery repository tests
- grocery integration flow with security and persistence
- stress runner aggregation test

Mockito usage includes:

- mocks
- spies
- captors
- argument matchers
- `doReturn`
- `doThrow`
- interaction verification

## Concurrency Decisions

The stress framework intentionally stays simple:

- one `ExecutorService` per stress run
- fixed thread pool sized by request concurrency
- `Callable` per HTTP request
- `Future` aggregation in the runner
- atomic counters for success/error/latency totals
- lock-protected latency and error sample lists for percentile calculation and report output

This is enough to demonstrate concurrency design without introducing a job system, message broker, or reactive stack.

## Trade-Offs and Simplifications

- single Spring Boot application instead of splitting modules into separate Maven artifacts
- in-memory async execution store instead of a persistent job table
- no pagination on grocery endpoints
- no advanced histogram library for percentiles
- no distributed load generation
- no production authentication provider; simple in-memory users are enough for the showcase
- no external migration tool like Flyway because H2 + JPA are sufficient for this scope

## Future Improvements

- persistent async execution history
- pagination and sorting for grocery listings
- richer converter metadata and field-level error reports
- optional CSV export for stress reports
- Micrometer/Actuator metrics
- richer retry/backoff configuration
- authenticated H2 console or disabled console by profile

## Files of Interest

- `pom.xml`
- `src/main/resources/application.yml`
- `src/main/java/com/example/taxsystem/grocery/application/GroceryService.java`
- `src/main/java/com/example/taxsystem/converter/application/ConverterEngine.java`
- `src/main/java/com/example/taxsystem/stresstest/infrastructure/HttpStressTestRunner.java`
- `src/main/java/com/example/taxsystem/common/config/SecurityConfig.java`

## Verification

Verified with:

```bash
mvn test
```
