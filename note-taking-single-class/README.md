# Note Taking Single Class

Java 8 Spring Boot note taking system with one production class.

Features:

- add notes
- save notes
- edit notes
- delete notes logically
- sync changed notes by timestamp

## Run

```bash
mvn spring-boot:run
```

## Test

```bash
mvn test
```

## API

Create:

```bash
curl -X POST http://localhost:8080/notes \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"Shopping\",\"content\":\"Milk\"}"
```

Edit:

```bash
curl -X PUT http://localhost:8080/notes/1 \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"Shopping\",\"content\":\"Milk and bread\"}"
```

Save:

```bash
curl -X POST http://localhost:8080/notes/1/save
```

Delete:

```bash
curl -X DELETE http://localhost:8080/notes/1
```

Sync:

```bash
curl "http://localhost:8080/notes/sync?updatedAfter=2026-05-27T10:00:00"
```
