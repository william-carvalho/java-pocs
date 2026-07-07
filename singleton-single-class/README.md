# Singleton Single Class

Java 8 POC for the GoF Singleton design pattern.

The production code is intentionally in one class:

```text
src/main/java/com/example/singleton/ApplicationSettings.java
```

## Features

- Private constructor.
- Static `getInstance()` access point.
- Lazy, thread-safe initialization through the initialization-on-demand holder idiom.
- Shared in-memory settings state.
- Synchronized reads and writes.
- Defensive read-only snapshots.

## Example

```java
ApplicationSettings settings = ApplicationSettings.getInstance();
settings.set("theme", "dark");

String theme = ApplicationSettings.getInstance().get("theme");
```

## Test

```bash
mvn test
```
