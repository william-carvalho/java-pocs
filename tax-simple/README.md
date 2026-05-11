# Tax Simple

Simple Java 8 Spring Boot project with one class.

It calculates product tax by:

- product
- state
- year

## Run

```bash
mvn spring-boot:run
```

## Example

```bash
curl "http://localhost:8080/tax?product=FOOD&state=SP&year=2024&price=100"
```

Response:

```json
{
  "product": "FOOD",
  "state": "SP",
  "year": 2024,
  "price": 100,
  "taxPercent": 0.07,
  "taxValue": 7.00,
  "finalPrice": 107.00
}
```
