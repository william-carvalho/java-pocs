# Hibernate Slow Query Single Class

Java 8 POC for detecting slow Hibernate-style queries.

The production code is intentionally in one class:

```text
src/main/java/com/example/slowquery/HibernateSlowQueryDetector.java
```

## Features

- Configurable slow-query threshold.
- `inspect(sql)` simulates Hibernate `StatementInspector` SQL capture.
- `monitor(source, work)` measures query execution blocks.
- Queries at or above the threshold are recorded.
- Slow query records include SQL, execution time, threshold, query type, source, and timestamp.
- In-memory history listing, lookup, clearing, and stats.

## Example

```java
HibernateSlowQueryDetector detector = new HibernateSlowQueryDetector(200);

detector.monitor("customerRepository.findAll", () -> {
    detector.inspect("select * from customers");
    // execute Hibernate/JPA query here
    return null;
});
```

## Test

```bash
mvn test
```
