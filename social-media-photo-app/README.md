# Social Media Photo App

POC simples em Java 8 com Spring Boot para publicar fotos, adicionar tags, comentar e visualizar uma timeline em ordem da mais recente para a mais antiga.

## Stack

- Java 8
- Spring Boot
- Spring Data JPA
- H2 em memória
- Storage local no filesystem
- Maven

## Regras da POC

- Usuário é obrigatório para publicar foto e comentar.
- A imagem é armazenada localmente em disco.
- Os metadados ficam no H2.
- O upload aceita apenas arquivos com `contentType` começando com `image/`.
- A timeline ignora fotos deletadas logicamente.
- Comentários deletados não aparecem por padrão.
- Tags são normalizadas em minúsculas.

## Estrutura

```text
src/main/java/com/example/socialmediaphotoapp
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── service
└── util
```

## Como rodar

```bash
mvn spring-boot:run
```

Aplicação sobe em `http://localhost:8080`.

H2 Console:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:socialdb`
- User: `sa`
- Password: vazio

## Configuração

`src/main/resources/application.properties`

```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:socialdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.h2.console.enabled=true
photo.storage.path=./storage/photos
```

## Dados iniciais

Usuários:

- william
- maria
- joao

Tags:

- travel
- food
- beach
- friends

## Endpoints

### POST /users

Payload:

```json
{
  "name": "William Carvalho",
  "username": "william"
}
```

```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "William Carvalho",
    "username": "william"
  }'
```

### GET /users

```bash
curl http://localhost:8080/users
```

### POST /photos

Multipart:

- `file`
- `userId`
- `caption` opcional

```bash
curl -X POST http://localhost:8080/photos \
  -F "file=@/caminho/foto.jpg" \
  -F "userId=1" \
  -F "caption=Minha primeira foto"
```

Resposta:

```json
{
  "id": 1,
  "user": {
    "id": 1,
    "name": "William Carvalho",
    "username": "william"
  },
  "caption": "Minha primeira foto",
  "originalFileName": "foto.jpg",
  "contentType": "image/jpeg",
  "createdAt": "2026-04-03T10:00:00",
  "tags": [],
  "commentsCount": 0
}
```

### GET /photos/{id}

```bash
curl http://localhost:8080/photos/1
```

### GET /photos/{id}/content

```bash
curl http://localhost:8080/photos/1/content --output foto.jpg
```

### POST /photos/{id}/tags

Payload:

```json
{
  "tags": ["travel", "beach", "sunset"]
}
```

```bash
curl -X POST http://localhost:8080/photos/1/tags \
  -H "Content-Type: application/json" \
  -d '{
    "tags": ["travel", "beach", "sunset"]
  }'
```

### GET /photos/{id}/comments

```bash
curl http://localhost:8080/photos/1/comments
```

### POST /photos/{id}/comments

Payload:

```json
{
  "userId": 1,
  "text": "Muito boa!"
}
```

```bash
curl -X POST http://localhost:8080/photos/1/comments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "text": "Muito boa!"
  }'
```

Resposta:

```json
{
  "id": 1,
  "photoId": 1,
  "user": {
    "id": 1,
    "name": "William Carvalho",
    "username": "william"
  },
  "text": "Muito boa!",
  "createdAt": "2026-04-03T10:05:00"
}
```

### GET /timeline

Filtros opcionais:

- `userId`
- `tag`
- `page`
- `size`

```bash
curl http://localhost:8080/timeline
```

```bash
curl "http://localhost:8080/timeline?userId=1&tag=travel&page=0&size=10"
```

Resposta:

```json
{
  "count": 1,
  "items": [
    {
      "photoId": 1,
      "user": {
        "id": 1,
        "name": "William Carvalho",
        "username": "william"
      },
      "caption": "Minha primeira foto",
      "createdAt": "2026-04-03T10:00:00",
      "tags": ["beach", "sunset", "travel"],
      "comments": [
        {
          "id": 1,
          "photoId": 1,
          "user": {
            "id": 1,
            "name": "William Carvalho",
            "username": "william"
          },
          "text": "Muito boa!",
          "createdAt": "2026-04-03T10:05:00"
        }
      ]
    }
  ]
}
```

### DELETE /photos/{id}

```bash
curl -X DELETE http://localhost:8080/photos/1
```

## Testes

```bash
mvn test
```
