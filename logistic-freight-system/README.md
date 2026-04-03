# Logistic Freight System

POC simples em Java 8 com Spring Boot para cadastro de regras de frete e calculo usando a regra vigente por tipo de transporte e data de referencia.

## Stack

- Java 8
- Spring Boot 2.7
- Maven
- Spring Web
- Spring Data JPA
- H2 em memoria

## Como rodar

```bash
mvn spring-boot:run
```

Aplicacao sobe em `http://localhost:8080`.

Console do H2:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:logisticdb`
- User: `sa`
- Password: vazio

## Como testar

Rodar testes:

```bash
mvn test
```

Listar tipos de transporte:

```bash
curl http://localhost:8080/freight/transport-types
```

Listar regras:

```bash
curl http://localhost:8080/freight/rules
```

Listar regras por tipo e data:

```bash
curl "http://localhost:8080/freight/rules?transportType=TRUCK&referenceDate=2026-04-01"
```

Cadastrar regra:

```bash
curl -X POST http://localhost:8080/freight/rules \
  -H "Content-Type: application/json" \
  -d '{
    "transportType": "TRUCK",
    "basePrice": 120.0,
    "pricePerVolumeUnit": 0.03,
    "pricePerWeightUnit": 0.55,
    "sizeMultiplier": 1.05,
    "effectiveFrom": "2026-07-01",
    "effectiveTo": "2026-12-31"
  }'
```

Calcular frete:

```bash
curl -X POST http://localhost:8080/freight/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "transportType": "TRUCK",
    "width": 2.0,
    "height": 1.5,
    "length": 3.0,
    "weight": 100.0,
    "referenceDate": "2026-04-01"
  }'
```

## Endpoints

- `POST /freight/rules`
- `GET /freight/rules`
- `POST /freight/calculate`
- `GET /freight/transport-types`

## Regras de negocio atendidas

- Uma regra valida por `transportType` para a mesma data
- Bloqueio de sobreposicao de vigencia para o mesmo `transportType`
- Erro claro quando nao existe regra valida para calculo
- Validacao de campos obrigatorios e valores invalidos
- Validacao de `effectiveFrom <= effectiveTo`

## Formula da POC

```text
volume = width * height * length
finalPrice = (basePrice + (volume * pricePerVolumeUnit) + (weight * pricePerWeightUnit)) * sizeMultiplier
```

## Dados iniciais

A aplicacao sobe com 3 regras carregadas automaticamente:

- `TRUCK` de `2026-01-01` ate `2026-06-30`
- `BOAT` de `2026-01-01` ate `2026-06-30`
- `RAIL` de `2026-01-01` ate `2026-06-30`

## Exemplos de payload

Criacao de regra:

```json
{
  "transportType": "TRUCK",
  "basePrice": 120.0,
  "pricePerVolumeUnit": 0.03,
  "pricePerWeightUnit": 0.55,
  "sizeMultiplier": 1.05,
  "effectiveFrom": "2026-07-01",
  "effectiveTo": "2026-12-31"
}
```

Calculo de frete:

```json
{
  "transportType": "TRUCK",
  "width": 2.0,
  "height": 1.5,
  "length": 3.0,
  "weight": 100.0,
  "referenceDate": "2026-04-01"
}
```
