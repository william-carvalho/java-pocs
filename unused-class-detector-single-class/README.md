# Unused Class Detector Single Class

Java 8 POC for detecting potentially unused Java classes through lightweight source scanning.

The production code is intentionally in one class:

```text
src/main/java/com/example/unusedclass/UnusedClassDetector.java
```

## Features

- Recursively scans `.java` files.
- Parses package and top-level class/interface/enum names.
- Builds a simple project-internal reference graph.
- Ignores self references.
- Treats main/application/config classes as roots.
- Supports custom ignore patterns.
- Reports classes with no incoming references as potentially unused.

## Example

```java
UnusedClassDetector detector = new UnusedClassDetector();

UnusedClassDetector.AnalysisResult result = detector.detect(
        UnusedClassDetector.config(Paths.get("src/main/java")));

System.out.println(result.toTextReport());
```

## Test

```bash
mvn test
```
