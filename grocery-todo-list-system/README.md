# Grocery TODO List System

POC simples em Java 8 com Spring Boot para uma lista de compras/TODO com criacao, remocao, listagem e mudanca de status.

## Stack

- Java 8
- Spring Boot 2.7
- Maven
- Spring Web
- Spring Data JPA
- H2 em memoria

## Visao rapida

- Adicionar item
- Remover item
- Marcar como done
- Marcar como do
- Re-do
- Listar todos os itens

Para a POC:

- `DO` coloca o item em `PENDING`
- `DONE` coloca o item em `DONE`
- `RE-DO` tambem coloca o item em `PENDING`

## Como rodar

```bash
mvn spring-boot:run
```

Aplicacao sobe em `http://localhost:8080`.

Console do H2:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:grocerydb`
- User: `sa`
- Password: vazio

## Como testar

```bash
mvn test
```

## Estrutura de pacotes

- `controller`
- `service`
- `repository`
- `entity`
- `dto`
- `exception`
- `config`
- `enums`

## Dados iniciais

- `Milk` / `2 liters` / `PENDING`
- `Bread` / `1 package` / `PENDING`
- `Eggs` / `12 units` / `DONE`

## Endpoints

- `POST /items`
- `GET /items`
- `GET /items/{id}`
- `DELETE /items/{id}`
- `PATCH /items/{id}/done`
- `PATCH /items/{id}/do`
- `PATCH /items/{id}/redo`

## Exemplos de payload

### POST /items

```json
{
  "name": "Milk",
  "quantity": "2 liters"
}
```

## Exemplos de curl

### Adicionar item

```bash
curl -X POST http://localhost:8080/items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Milk",
    "quantity": "2 liters"
  }'
```

### Listar itens

```bash
curl http://localhost:8080/items
```

### Listar itens filtrando por status

```bash
curl "http://localhost:8080/items?status=PENDING"
```

### Consultar item por id

```bash
curl http://localhost:8080/items/1
```

### Marcar como done

```bash
curl -X PATCH http://localhost:8080/items/1/done
```

### Marcar como do

```bash
curl -X PATCH http://localhost:8080/items/1/do
```

### Re-do

```bash
curl -X PATCH http://localhost:8080/items/1/redo
```

### Remover

```bash
curl -X DELETE http://localhost:8080/items/2
```

## Exemplo de resposta

```json
{
  "id": 1,
  "name": "Milk",
  "quantity": "2 liters",
  "status": "PENDING",
  "createdAt": "2026-04-03T10:00:00",
  "updatedAt": "2026-04-03T10:00:00"
}
```

## Regras de negocio atendidas

- Nome do item obrigatorio
- Nome vazio nao e permitido
- `quantity` opcional
- Item criado sempre como `PENDING`
- `done` altera para `DONE`
- `do` altera para `PENDING`
- `redo` altera para `PENDING`
- `remove` exclui o item
- `listAll` lista todos os itens
- Filtro opcional por `status`
- Erro claro para item inexistente
