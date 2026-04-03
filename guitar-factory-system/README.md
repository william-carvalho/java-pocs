# Guitar Factory System

POC simples em Java 8 com Spring Boot para cadastro de modelos, componentes, pedidos de guitarras customizadas, OS de producao e controle de inventario.

## Stack

- Java 8
- Spring Boot 2.7
- Maven
- Spring Web
- Spring Data JPA
- H2 em memoria

## Visao rapida

- Cadastro de modelos base de guitarra
- Cadastro de componentes e especificacoes
- Consulta de inventario atual
- Criacao de pedido customizado com validacao de estoque
- Geracao de OS para producao
- Acompanhamento do status da OS

Para esta POC, o estoque e debitado na criacao do pedido customizado. Se a OS for cancelada, o estoque dos componentes do pedido e devolvido.

## Como rodar

```bash
mvn spring-boot:run
```

Aplicacao sobe em `http://localhost:8080`.

Console do H2:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:guitardb`
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

Modelos:

- `Strat Style` / `2500`
- `Tele Style` / `2300`
- `Les Paul Style` / `3200`

Componentes:

- `Alder Body` / `BODY_WOOD` / estoque `10` / `500`
- `Maple Neck` / `NECK_WOOD` / estoque `10` / `700`
- `Single Coil Pickup` / `PICKUP` / estoque `30` / `300`
- `Humbucker Pickup` / `PICKUP` / estoque `20` / `450`
- `Vintage Bridge` / `BRIDGE` / estoque `15` / `350`
- `Black Finish` / `FINISH` / estoque `20` / `150`
- `Standard Strings` / `STRINGS` / estoque `50` / `50`

## Endpoints

- `POST /models`
- `GET /models`
- `POST /components`
- `GET /components`
- `GET /inventory`
- `POST /custom-guitars`
- `GET /custom-guitars`
- `GET /custom-guitars/{id}`
- `POST /work-orders`
- `GET /work-orders`
- `GET /work-orders/{id}`
- `PATCH /work-orders/{id}/status`

## Exemplos de payload

### POST /models

```json
{
  "name": "SG Style",
  "description": "Slim body custom platform",
  "basePrice": 2800.00
}
```

### POST /components

```json
{
  "name": "Gold Tuner Set",
  "category": "TUNER",
  "specificationValue": "Gold",
  "quantityInStock": 12,
  "unitPrice": 220.00
}
```

### POST /custom-guitars

```json
{
  "customerName": "William Carvalho",
  "guitarModelId": 1,
  "items": [
    {
      "componentId": 1,
      "quantity": 1
    },
    {
      "componentId": 2,
      "quantity": 1
    },
    {
      "componentId": 3,
      "quantity": 2
    }
  ]
}
```

### POST /work-orders

```json
{
  "customGuitarOrderId": 1
}
```

### PATCH /work-orders/{id}/status

```json
{
  "status": "IN_PROGRESS"
}
```

## Exemplos de curl

### Cadastrar modelo

```bash
curl -X POST http://localhost:8080/models \
  -H "Content-Type: application/json" \
  -d '{
    "name": "SG Style",
    "description": "Slim body custom platform",
    "basePrice": 2800.00
  }'
```

### Listar modelos

```bash
curl http://localhost:8080/models
```

### Cadastrar componente

```bash
curl -X POST http://localhost:8080/components \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gold Tuner Set",
    "category": "TUNER",
    "specificationValue": "Gold",
    "quantityInStock": 12,
    "unitPrice": 220.00
  }'
```

### Listar componentes

```bash
curl http://localhost:8080/components
```

### Consultar inventario

```bash
curl http://localhost:8080/inventory
```

### Criar pedido customizado

```bash
curl -X POST http://localhost:8080/custom-guitars \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "William Carvalho",
    "guitarModelId": 1,
    "items": [
      {
        "componentId": 1,
        "quantity": 1
      },
      {
        "componentId": 2,
        "quantity": 1
      },
      {
        "componentId": 3,
        "quantity": 2
      }
    ]
  }'
```

### Listar pedidos customizados

```bash
curl http://localhost:8080/custom-guitars
```

### Consultar pedido customizado

```bash
curl http://localhost:8080/custom-guitars/1
```

### Criar OS

```bash
curl -X POST http://localhost:8080/work-orders \
  -H "Content-Type: application/json" \
  -d '{
    "customGuitarOrderId": 1
  }'
```

### Listar OS

```bash
curl http://localhost:8080/work-orders
```

### Consultar OS

```bash
curl http://localhost:8080/work-orders/1
```

### Atualizar status da OS

```bash
curl -X PATCH http://localhost:8080/work-orders/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "FINISHED"
  }'
```

## Exemplo de resposta de pedido customizado

```json
{
  "orderId": 1,
  "customerName": "William Carvalho",
  "guitarModel": {
    "id": 1,
    "name": "Strat Style"
  },
  "status": "CREATED",
  "createdAt": "2026-04-03T10:00:00",
  "items": [
    {
      "componentId": 1,
      "componentName": "Alder Body",
      "category": "BODY_WOOD",
      "quantity": 1,
      "unitPrice": 500.00,
      "totalPrice": 500.00
    },
    {
      "componentId": 2,
      "componentName": "Single Coil Pickup",
      "category": "PICKUP",
      "quantity": 2,
      "unitPrice": 300.00,
      "totalPrice": 600.00
    }
  ],
  "basePrice": 2500.00,
  "totalPrice": 3600.00,
  "workOrderId": 1,
  "workOrderNumber": "WO-00001"
}
```

## Regras de negocio atendidas

- Pedido customizado sempre referencia um modelo base
- Pedido sempre contem componentes selecionados
- Todos os componentes precisam existir
- O sistema bloqueia pedido com estoque insuficiente
- O estoque e debitado no momento da criacao do pedido customizado
- O total considera `basePrice` do modelo + componentes
- A OS acompanha status de producao
- Cancelamento da OS devolve estoque e cancela o pedido

## Formula de preco

```text
totalPrice = guitarModel.basePrice + soma(component.unitPrice * quantity)
```
