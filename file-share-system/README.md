# File Share System

POC simples em Java 8 com Spring Boot para upload, listagem, busca, restore e deleção lógica de arquivos com conteúdo armazenado criptografado em disco local.

## Stack

- Java 8
- Spring Boot
- Spring Data JPA
- H2 em memória
- Storage local com `java.nio.file`
- AES simples com chave configurada em `application.properties`

## Decisões da POC

- Os metadados ficam no H2.
- O conteúdo do arquivo é salvo criptografado no filesystem local.
- O restore descriptografa o conteúdo apenas no momento do download.
- O delete é lógico: marca `deleted = true` no banco e mantém o arquivo físico em disco.
- A busca por nome e a listagem ignoram arquivos deletados.

## Estrutura

```text
src/main/java/com/example/filesharesystem
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
- JDBC URL: `jdbc:h2:mem:filesharedb`
- User: `sa`
- Password: vazio

## Configuração

`src/main/resources/application.properties`

```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:filesharedb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.h2.console.enabled=true
file.storage.path=./storage
file.encryption.key=1234567890123456
```

Observação: a chave AES desta POC é fixa por configuração. É intencionalmente simples para facilitar demonstração.

## Endpoints

### 1. Upload de arquivo

`POST /files`

Recebe `multipart/form-data` com o campo `file`.

Exemplo:

```bash
curl -X POST http://localhost:8080/files \
  -F "file=@/caminho/contract.pdf"
```

Resposta:

```json
{
  "id": 1,
  "originalFileName": "contract.pdf",
  "contentType": "application/pdf",
  "size": 234567,
  "encrypted": true,
  "createdAt": "2026-04-03T10:00:00"
}
```

### 2. Listar arquivos

`GET /files`

Exemplo:

```bash
curl http://localhost:8080/files
```

Resposta:

```json
[
  {
    "id": 1,
    "originalFileName": "contract.pdf",
    "contentType": "application/pdf",
    "size": 234567,
    "encrypted": true,
    "createdAt": "2026-04-03T10:00:00"
  }
]
```

### 3. Consultar metadados

`GET /files/{id}`

Exemplo:

```bash
curl http://localhost:8080/files/1
```

Resposta:

```json
{
  "id": 1,
  "originalFileName": "contract.pdf",
  "contentType": "application/pdf",
  "size": 234567,
  "encrypted": true,
  "createdAt": "2026-04-03T10:00:00"
}
```

### 4. Restaurar arquivo

`GET /files/{id}/restore`

Exemplo:

```bash
curl -X GET http://localhost:8080/files/1/restore --output contract.pdf
```

Retorna o arquivo descriptografado com `Content-Disposition` para download.

### 5. Deletar arquivo

`DELETE /files/{id}`

Exemplo:

```bash
curl -X DELETE http://localhost:8080/files/1
```

Resposta: `204 No Content`

### 6. Buscar arquivo por nome

`GET /files/search?name=contract`

Exemplo:

```bash
curl "http://localhost:8080/files/search?name=contract"
```

Resposta:

```json
[
  {
    "id": 1,
    "originalFileName": "contract.pdf",
    "contentType": "application/pdf",
    "size": 234567,
    "encrypted": true,
    "createdAt": "2026-04-03T10:00:00"
  }
]
```

## Regras principais

- Upload vazio retorna erro.
- Restore não permite arquivo deletado.
- Busca e listagem ignoram deletados.
- O conteúdo persistido no disco é realmente criptografado.
- O nome físico do arquivo é gerado com UUID.

## Testes

```bash
mvn test
```
