# Restaurant Queue System

POC simples em Java 8 com Spring Boot para cadastro de pratos, criacao de pedidos e estimativa de fila de preparo.

## Stack

- Java 8
- Spring Boot 2.7
- Maven
- Spring Web
- Spring Data JPA
- H2 em memoria

## Visao rapida

O sistema trabalha com uma fila unica de preparo.

- Cada prato possui um tempo base em minutos
- Cada item do pedido calcula `tempo do item = tempo do prato * quantidade`
- O total do pedido e a soma dos itens
- A estimativa de inicio considera os pedidos `WAITING` e `IN_PROGRESS` que estao na frente
- Pedidos `DONE` e `CANCELLED` saem do impacto da fila

## Como rodar

```bash
mvn spring-boot:run
```

Aplicacao sobe em `http://localhost:8080`.

Console do H2:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:restaurantdb`
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

Pratos carregados automaticamente ao subir:

- `Burger` / `15`
- `Pizza` / `20`
- `Salad` / `8`
- `Pasta` / `18`

## Endpoints

- `POST /dishes`
- `GET /dishes`
- `POST /orders`
- `GET /orders/{id}`
- `PATCH /orders/{id}/status`
- `GET /orders/{id}/estimate`
- `GET /queue`

## Exemplos de payload

### POST /dishes

```json
{
  "name": "Risotto",
  "preparationTimeMinutes": 22
}
```

### POST /orders

```json
{
  "customerName": "William",
  "items": [
    {
      "dishId": 1,
      "quantity": 2
    },
    {
      "dishId": 3,
      "quantity": 1
    }
  ]
}
```

### PATCH /orders/{id}/status

```json
{
  "status": "IN_PROGRESS"
}
```

## Exemplos de curl

### Cadastrar prato

```bash
curl -X POST http://localhost:8080/dishes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Risotto",
    "preparationTimeMinutes": 22
  }'
```

### Listar pratos

```bash
curl http://localhost:8080/dishes
```

### Criar pedido

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "William",
    "items": [
      {
        "dishId": 1,
        "quantity": 2
      },
      {
        "dishId": 3,
        "quantity": 1
      }
    ]
  }'
```

### Consultar pedido

```bash
curl http://localhost:8080/orders/1
```

### Consultar estimativa do pedido

```bash
curl http://localhost:8080/orders/1/estimate
```

### Atualizar status do pedido

```bash
curl -X PATCH http://localhost:8080/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "DONE"
  }'
```

### Listar fila

```bash
curl http://localhost:8080/queue
```

## Exemplo de resposta de pedido

```json
{
  "orderId": 1,
  "customerName": "William",
  "status": "WAITING",
  "createdAt": "2026-04-03T15:00:00",
  "queuePosition": 1,
  "items": [
    {
      "dishId": 1,
      "dishName": "Burger",
      "quantity": 2,
      "unitPreparationTime": 15,
      "totalItemPreparationTime": 30
    },
    {
      "dishId": 3,
      "dishName": "Salad",
      "quantity": 1,
      "unitPreparationTime": 8,
      "totalItemPreparationTime": 8
    }
  ],
  "totalPreparationTime": 38,
  "estimatedStartInMinutes": 0,
  "estimatedCompletionInMinutes": 38,
  "remainingTimeInMinutes": 38
}
```

## Exemplo de resposta da fila

```json
[
  {
    "orderId": 1,
    "customerName": "William",
    "status": "WAITING",
    "createdAt": "2026-04-03T15:00:00",
    "queuePosition": 1,
    "totalPreparationTime": 23,
    "estimatedStartInMinutes": 0,
    "estimatedCompletionInMinutes": 23,
    "remainingTimeInMinutes": 23
  },
  {
    "orderId": 2,
    "customerName": "Maria",
    "status": "WAITING",
    "createdAt": "2026-04-03T15:01:00",
    "queuePosition": 2,
    "totalPreparationTime": 40,
    "estimatedStartInMinutes": 23,
    "estimatedCompletionInMinutes": 63,
    "remainingTimeInMinutes": 63
  }
]
```

## Regras de negocio atendidas

- Tempo base de prato maior que zero
- Quantidade do item maior que zero
- Pedido entra com status `WAITING`
- Apenas `WAITING` e `IN_PROGRESS` impactam a fila
- `DONE` e `CANCELLED` nao entram no recalculo da fila
- Ordenacao da fila por `createdAt` e `id`
- Erro claro para prato inexistente
- Erro claro para pedido inexistente

## Formula da POC

```text
totalItemPreparationTime = dishPreparationTimeMinutes * quantity
totalPreparationTime = soma dos itens
estimatedStartInMinutes = soma dos pedidos anteriores ativos
estimatedCompletionInMinutes = estimatedStartInMinutes + totalPreparationTime
```
