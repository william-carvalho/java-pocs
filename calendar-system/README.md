# Calendar System

POC simples em Java 8 com Spring Boot para cadastrar pessoas, agendar reuniões, cancelar reuniões, listar agenda e sugerir o primeiro melhor horário disponível para 2 pessoas.

## Stack

- Java 8
- Spring Boot
- Spring Data JPA
- H2 em memória
- Maven

## Regras da POC

- Cada reunião possui organizador, participantes, início e fim.
- Reuniões com `status = CANCELLED` não entram em conflito e não impactam a sugestão.
- Uma pessoa não pode ter duas reuniões sobrepostas.
- A sugestão retorna o primeiro slot livre em comum dentro da janela informada.
- O cancelamento é lógico: a reunião continua registrada, mas com status `CANCELLED`.

## Estrutura

```text
src/main/java/com/example/calendarsystem
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
└── service
```

## Como rodar

```bash
mvn spring-boot:run
```

Aplicação sobe em `http://localhost:8080`.

H2 Console:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:calendardb`
- User: `sa`
- Password: vazio

## Dados iniciais

Pessoas:

- William
- Ricardo
- Fabio

Reuniões:

- William + Ricardo / `2026-04-03T09:00:00` às `10:00:00`
- William / `2026-04-03T13:00:00` às `14:00:00`
- Ricardo / `2026-04-03T10:00:00` às `11:00:00`

## Endpoints

### 1. Criar pessoa

`POST /people`

Payload:

```json
{
  "name": "William",
  "email": "william@example.com"
}
```

Curl:

```bash
curl -X POST http://localhost:8080/people \
  -H "Content-Type: application/json" \
  -d '{
    "name": "William",
    "email": "william@example.com"
  }'
```

### 2. Listar pessoas

`GET /people`

Curl:

```bash
curl http://localhost:8080/people
```

### 3. Criar reunião

`POST /meetings`

Payload:

```json
{
  "title": "Tech Alignment",
  "description": "Weekly sync",
  "organizerId": 1,
  "participantIds": [2],
  "startDateTime": "2026-04-03T10:00:00",
  "endDateTime": "2026-04-03T11:00:00"
}
```

Curl:

```bash
curl -X POST http://localhost:8080/meetings \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Tech Alignment",
    "description": "Weekly sync",
    "organizerId": 1,
    "participantIds": [2],
    "startDateTime": "2026-04-03T10:00:00",
    "endDateTime": "2026-04-03T11:00:00"
  }'
```

### 4. Listar reuniões

`GET /meetings`

Filtros opcionais:

- `personId`
- `status`
- `date`

Curl:

```bash
curl http://localhost:8080/meetings
```

Listar reuniões de uma pessoa:

```bash
curl "http://localhost:8080/meetings?personId=1"
```

### 5. Consultar reunião por id

`GET /meetings/{id}`

Curl:

```bash
curl http://localhost:8080/meetings/1
```

### 6. Cancelar reunião

`DELETE /meetings/{id}`

Curl:

```bash
curl -X DELETE http://localhost:8080/meetings/1
```

Resposta: `204 No Content`

### 7. Sugerir horário

`POST /meetings/suggest`

Payload:

```json
{
  "personOneId": 1,
  "personTwoId": 2,
  "searchStart": "2026-04-03T09:00:00",
  "searchEnd": "2026-04-03T18:00:00",
  "durationMinutes": 60
}
```

Curl:

```bash
curl -X POST http://localhost:8080/meetings/suggest \
  -H "Content-Type: application/json" \
  -d '{
    "personOneId": 1,
    "personTwoId": 2,
    "searchStart": "2026-04-03T09:00:00",
    "searchEnd": "2026-04-03T18:00:00",
    "durationMinutes": 60
  }'
```

Resposta:

```json
{
  "personOneId": 1,
  "personTwoId": 2,
  "suggestedStart": "2026-04-03T11:00:00",
  "suggestedEnd": "2026-04-03T12:00:00",
  "durationMinutes": 60,
  "message": "First available slot found"
}
```

## Testes

```bash
mvn test
```
