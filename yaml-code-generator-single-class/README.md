# YAML Code Generator Single Class

Java 8 Spring Boot code generator based on YAML definitions with one production class.

Features:

- parse YAML definitions
- validate package, classes, and fields
- generate Java class source in memory
- support no-args constructor, all-args constructor, getters, and setters

## Run

```bash
mvn spring-boot:run
```

## Test

```bash
mvn test
```

## API

Generate Java files:

```bash
curl -X POST http://localhost:8080/generate \
  -H "Content-Type: text/plain" \
  --data-binary @definition.yml
```

Example YAML:

```yaml
codegen:
  basePackage: com.example.generated
  classes:
    - name: User
      generateGettersSetters: true
      generateNoArgsConstructor: true
      generateAllArgsConstructor: true
      fields:
        - name: id
          type: Long
        - name: name
          type: String
```
