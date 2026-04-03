# Ticket System

POC simples em Java 8 com Spring Boot para cadastro de shows, venues, zonas, sessoes e venda de ingressos com validacao de assentos e capacidade.

## Stack

- Java 8
- Spring Boot 2.7
- Maven
- Spring Web
- Spring Data JPA
- H2 em memoria

## Visao rapida

- Cadastro de shows
- Cadastro de venues
- Cadastro de zonas por venue
- Criacao de sessoes por show, venue e data
- Venda de ingressos com escolha de assento
- Consulta de disponibilidade por zona
- Cancelamento de ticket liberando o assento novamente

## Como rodar

```bash
mvn spring-boot:run
```

Aplicacao sobe em `http://localhost:8080`.

Console do H2:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:ticketdb`
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

Shows:

- `Rock Night`
- `Jazz Festival`
- `Pop Experience`

Venues:

- `Arena Floripa`
- `Teatro Central`

Zonas iniciais:

- Arena Floripa: `VIP` A1-A50, `PREMIUM` B1-B100, `STANDARD` C1-C300
- Teatro Central: `PLATEA` D1-D80, `BALCONY` E1-E120

Sessoes:

- `Rock Night` / `Arena Floripa` / `2026-04-20T20:00:00`
- `Jazz Festival` / `Teatro Central` / `2026-04-25T21:00:00`

## Endpoints

- `POST /shows`
- `GET /shows`
- `POST /venues`
- `GET /venues`
- `POST /venues/{venueId}/zones`
- `GET /venues/{venueId}/zones`
- `POST /sessions`
- `GET /sessions`
- `GET /sessions/{sessionId}/availability`
- `POST /tickets/sell`
- `GET /tickets`
- `GET /tickets/{id}`
- `PATCH /tickets/{id}/cancel`

## Exemplos de payload

### POST /shows

```json
{
  "name": "Acoustic Sessions",
  "description": "Intimate live performance"
}
```

### POST /venues

```json
{
  "name": "Clube Downtown",
  "city": "Sao Paulo"
}
```

### POST /venues/{venueId}/zones

```json
{
  "name": "VIP",
  "maxCapacity": 50,
  "seatPrefix": "A",
  "seatStart": 1,
  "seatEnd": 50
}
```

### POST /sessions

```json
{
  "showId": 1,
  "venueId": 1,
  "eventDateTime": "2026-04-20T20:00:00"
}
```

### POST /tickets/sell

```json
{
  "sessionId": 1,
  "customerName": "William Carvalho",
  "items": [
    {
      "zoneId": 1,
      "seatNumber": "A10"
    },
    {
      "zoneId": 1,
      "seatNumber": "A11"
    }
  ]
}
```

## Exemplos de curl

### Cadastrar show

```bash
curl -X POST http://localhost:8080/shows \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Acoustic Sessions",
    "description": "Intimate live performance"
  }'
```

### Listar shows

```bash
curl http://localhost:8080/shows
```

### Cadastrar venue

```bash
curl -X POST http://localhost:8080/venues \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Clube Downtown",
    "city": "Sao Paulo"
  }'
```

### Listar venues

```bash
curl http://localhost:8080/venues
```

### Cadastrar zona

```bash
curl -X POST http://localhost:8080/venues/1/zones \
  -H "Content-Type: application/json" \
  -d '{
    "name": "VIP",
    "maxCapacity": 50,
    "seatPrefix": "A",
    "seatStart": 1,
    "seatEnd": 50
  }'
```

### Listar zonas do venue

```bash
curl http://localhost:8080/venues/1/zones
```

### Criar sessao

```bash
curl -X POST http://localhost:8080/sessions \
  -H "Content-Type: application/json" \
  -d '{
    "showId": 1,
    "venueId": 1,
    "eventDateTime": "2026-04-20T20:00:00"
  }'
```

### Listar sessoes

```bash
curl http://localhost:8080/sessions
```

### Listar sessoes filtrando

```bash
curl "http://localhost:8080/sessions?showId=1&date=2026-04-20"
```

### Consultar disponibilidade

```bash
curl http://localhost:8080/sessions/1/availability
```

### Vender ingressos

```bash
curl -X POST http://localhost:8080/tickets/sell \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": 1,
    "customerName": "William Carvalho",
    "items": [
      {
        "zoneId": 1,
        "seatNumber": "A10"
      },
      {
        "zoneId": 1,
        "seatNumber": "A11"
      }
    ]
  }'
```

### Listar tickets

```bash
curl http://localhost:8080/tickets
```

### Listar tickets com filtros

```bash
curl "http://localhost:8080/tickets?sessionId=1&zoneId=1&status=SOLD"
```

### Consultar ticket

```bash
curl http://localhost:8080/tickets/1
```

### Cancelar ticket

```bash
curl -X PATCH http://localhost:8080/tickets/1/cancel
```

## Exemplo de resposta de venda

```json
{
  "sessionId": 1,
  "customerName": "William Carvalho",
  "tickets": [
    {
      "ticketId": 101,
      "showName": "Rock Night",
      "eventDateTime": "2026-04-20T20:00:00",
      "venueName": "Arena Floripa",
      "zoneId": 1,
      "zoneName": "VIP",
      "seatNumber": "A10",
      "status": "SOLD"
    },
    {
      "ticketId": 102,
      "showName": "Rock Night",
      "eventDateTime": "2026-04-20T20:00:00",
      "venueName": "Arena Floripa",
      "zoneId": 1,
      "zoneName": "VIP",
      "seatNumber": "A11",
      "status": "SOLD"
    }
  ],
  "totalTickets": 2
}
```

## Exemplo de resposta de disponibilidade

```json
{
  "sessionId": 1,
  "showName": "Rock Night",
  "eventDateTime": "2026-04-20T20:00:00",
  "zones": [
    {
      "zoneId": 1,
      "zoneName": "VIP",
      "maxCapacity": 50,
      "soldCount": 2,
      "availableCount": 48,
      "occupiedSeats": ["A10", "A11"]
    }
  ]
}
```

## Regras de negocio atendidas

- Assento precisa estar dentro do range da zona
- Assento nao pode ser vendido duas vezes na mesma sessao e zona
- Venda respeita capacidade maxima da zona
- Venda respeita capacidade total da sessao
- Zona precisa pertencer ao venue da sessao
- Cancelamento libera o assento novamente

