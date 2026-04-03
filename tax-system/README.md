# Tax System POC

POC simples em Java 8 com Spring Boot para cadastro e consulta de regras de imposto e calculo de tax por produto, estado e ano.

## Stack

- Java 8
- Spring Boot 2.7
- Spring Web
- Spring Data JPA
- H2 em memoria

## Como subir

### Requisitos

- Java 8+
- Maven 3.8+

### Rodando a aplicacao

```bash
mvn spring-boot:run
```

Aplicacao sobe em `http://localhost:8080`.

H2 Console: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:taxdb`
- User: `sa`
- Password: vazio

## Endpoints

### 1. Criar regra

`POST /tax-rules`

Payload:

```json
{
  "productCode": "PRODUCT_D",
  "state": "SP",
  "year": 2026,
  "taxPercent": 0.17
}
```

### 2. Listar regras

`GET /tax-rules`

Filtros opcionais:

- `productCode`
- `state`
- `year`

Exemplos:

```bash
curl "http://localhost:8080/tax-rules"
curl "http://localhost:8080/tax-rules?productCode=PRODUCT_A"
curl "http://localhost:8080/tax-rules?productCode=PRODUCT_A&state=SP&year=2024"
```

### 3. Calcular imposto

`POST /tax/calculate`

Payload:

```json
{
  "productCode": "PRODUCT_A",
  "state": "SP",
  "year": 2024,
  "baseAmount": 1000
}
```

Resposta esperada:

```json
{
  "productCode": "PRODUCT_A",
  "state": "SP",
  "year": 2024,
  "baseAmount": 1000.00,
  "taxPercent": 0.1800,
  "taxValue": 180.00,
  "totalAmount": 1180.00
}
```

## Dados iniciais

Ao subir a aplicacao, as regras abaixo sao carregadas automaticamente:

- `PRODUCT_A / SP / 2024 / 0.18`
- `PRODUCT_A / RJ / 2024 / 0.20`
- `PRODUCT_B / SC / 2025 / 0.12`
- `PRODUCT_C / MG / 2023 / 0.15`

## Regras de negocio implementadas

- Cada combinacao `productCode + state + year` e unica
- Se nao existir regra cadastrada, a API retorna `404`
- O calculo segue:
  - `taxValue = baseAmount * taxPercent`
  - `totalAmount = baseAmount + taxValue`

## Exemplos de teste com curl

### Criar regra

```bash
curl -X POST http://localhost:8080/tax-rules \
  -H "Content-Type: application/json" \
  -d "{\"productCode\":\"PRODUCT_D\",\"state\":\"SC\",\"year\":2026,\"taxPercent\":0.14}"
```

### Listar regras

```bash
curl http://localhost:8080/tax-rules
```

### Calcular imposto

```bash
curl -X POST http://localhost:8080/tax/calculate \
  -H "Content-Type: application/json" \
  -d "{\"productCode\":\"PRODUCT_A\",\"state\":\"SP\",\"year\":2024,\"baseAmount\":1000}"
```

### Regra inexistente

```bash
curl -X POST http://localhost:8080/tax/calculate \
  -H "Content-Type: application/json" \
  -d "{\"productCode\":\"PRODUCT_X\",\"state\":\"SP\",\"year\":2024,\"baseAmount\":1000}"
```

Resposta:

```json
{
  "timestamp": "2026-04-03T14:00:00",
  "status": 404,
  "error": "Not Found",
  "messages": [
    "No tax rule found for productCode=PRODUCT_X, state=SP, year=2024"
  ]
}
```
