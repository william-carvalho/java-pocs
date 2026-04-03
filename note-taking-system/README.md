# Note Taking System

POC simples em Java 8 com Spring Boot para criar, editar, remover logicamente e sincronizar notas com base em `updatedAt`.

## Stack

- Java 8
- Spring Boot
- Spring Data JPA
- H2 em memória
- Maven

## Regras da POC

- `title` é obrigatório.
- `content` é obrigatório.
- A nota nasce com status `ACTIVE`, `deleted = false` e `version = 1`.
- Edição incrementa `version` e atualiza `updatedAt`.
- Delete é lógico: marca `deleted = true`, incrementa `version` e atualiza `updatedAt`.
- `sync` retorna tudo que mudou desde um timestamp, inclusive notas deletadas logicamente.

## Estrutura

```text
src/main/java/com/example/notetakingsystem
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
- JDBC URL: `jdbc:h2:mem:notedb`
- User: `sa`
- Password: vazio

## Dados iniciais

Ao subir a aplicação, a POC cria:

- `Welcome` / `This is your first note`
- `Tasks` / `Finish POC`
- `Ideas` / `Build sync endpoint`

## Endpoints

### 1. Criar nota

`POST /notes`

Payload:

```json
{
  "title": "Shopping List",
  "content": "Milk, Bread, Eggs"
}
```

Curl:

```bash
curl -X POST http://localhost:8080/notes \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Shopping List",
    "content": "Milk, Bread, Eggs"
  }'
```

Resposta:

```json
{
  "id": 1,
  "title": "Shopping List",
  "content": "Milk, Bread, Eggs",
  "status": "ACTIVE",
  "deleted": false,
  "createdAt": "2026-04-03T10:00:00",
  "updatedAt": "2026-04-03T10:00:00",
  "version": 1
}
```

### 2. Listar notas ativas

`GET /notes`

Curl:

```bash
curl http://localhost:8080/notes
```

### 3. Listar incluindo deletadas

`GET /notes?includeDeleted=true`

Curl:

```bash
curl "http://localhost:8080/notes?includeDeleted=true"
```

### 4. Listar tudo

`GET /notes/all`

Curl:

```bash
curl http://localhost:8080/notes/all
```

### 5. Consultar nota por id

`GET /notes/{id}`

Curl:

```bash
curl http://localhost:8080/notes/1
```

### 6. Editar nota

`PUT /notes/{id}`

Payload:

```json
{
  "title": "Shopping List Updated",
  "content": "Milk, Bread, Eggs, Coffee"
}
```

Curl:

```bash
curl -X PUT http://localhost:8080/notes/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Shopping List Updated",
    "content": "Milk, Bread, Eggs, Coffee"
  }'
```

### 7. Deletar nota

`DELETE /notes/{id}`

Curl:

```bash
curl -X DELETE http://localhost:8080/notes/1
```

Resposta: `204 No Content`

### 8. Sync de notas

`GET /notes/sync?updatedAfter=2026-04-03T09:00:00`

Curl:

```bash
curl "http://localhost:8080/notes/sync?updatedAfter=2026-04-03T09:00:00"
```

Resposta:

```json
{
  "updatedAfter": "2026-04-03T09:00:00",
  "count": 2,
  "notes": [
    {
      "id": 1,
      "title": "Shopping List Updated",
      "content": "Milk, Bread, Eggs, Coffee",
      "status": "ACTIVE",
      "deleted": false,
      "createdAt": "2026-04-03T10:00:00",
      "updatedAt": "2026-04-03T10:05:00",
      "version": 2
    },
    {
      "id": 2,
      "title": "Old Note",
      "content": "to be removed",
      "status": "ACTIVE",
      "deleted": true,
      "createdAt": "2026-04-03T08:00:00",
      "updatedAt": "2026-04-03T10:06:00",
      "version": 3
    }
  ]
}
```

## Testes

```bash
mvn test
```
