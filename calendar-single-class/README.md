# Calendar Single Class

Java 8 Spring Boot calendar system with one production class.

Features:

- book meetings
- remove meetings
- list meetings
- suggest the first available time for two people

## Run

```bash
mvn spring-boot:run
```

## Test

```bash
mvn test
```

## API

Book:

```bash
curl -X POST http://localhost:8080/meetings \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"Planning\",\"people\":[\"ana\",\"bob\"],\"start\":\"2026-05-28T09:00:00\",\"end\":\"2026-05-28T10:00:00\"}"
```

List:

```bash
curl "http://localhost:8080/meetings?person=ana&date=2026-05-28"
```

Remove:

```bash
curl -X DELETE http://localhost:8080/meetings/1
```

Suggest:

```bash
curl -X POST http://localhost:8080/meetings/suggest \
  -H "Content-Type: application/json" \
  -d "{\"people\":[\"ana\",\"bob\"],\"searchStart\":\"2026-05-28T09:00:00\",\"searchEnd\":\"2026-05-28T18:00:00\",\"durationMinutes\":60}"
```
