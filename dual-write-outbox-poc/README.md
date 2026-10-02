# Dual Write + Transactional Outbox POC

POC em Java 8 e Spring Boot que reproduz o **Dual Write Problem** e mostra como o
**Transactional Outbox Pattern** evita a perda do evento de negocio.

Inspirada no video
[Dual Write | O problema que mais reprova programadores nas entrevistas](https://www.youtube.com/watch?v=voeWcHeYzLI),
de Renato Augusto.

O exemplo usa cadastro de usuario + publicacao do evento `UserRegistered`. O H2
representa o banco e um broker em memoria representa Kafka/RabbitMQ, permitindo
simular falhas de forma deterministica sem Docker.

## O que a POC demonstra

### 1. Banco primeiro (inconsistente)

```text
COMMIT user_accounts -> falha ao publicar
Resultado: usuario existe, mensagem nao existe
```

### 2. Mensagem primeiro (inconsistente)

```text
publica mensagem -> ROLLBACK user_accounts
Resultado: mensagem fantasma, usuario nao existe
```

### 3. Transactional Outbox

```text
Transacao local: user_accounts + outbox_events
                              |
                         Message Relay
                              |
                            Broker
```

O usuario e o evento entram na mesma transacao do H2. Um relay separado tenta
publicar eventos pendentes. Em caso de falha eles continuam `PENDING` e podem ser
reenviados. Como a entrega e *at-least-once*, o broker deduplica pelo `eventId`.

## Executar

Requisitos: Java 8+ e Maven 3.8+.

```bash
cd dual-write-outbox-poc
mvn spring-boot:run
```

API: `http://localhost:8080`

H2 Console: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:dualwrite`
- User: `sa`
- Password: vazio

## Roteiro da demonstracao

Os exemplos abaixo usam `curl`. Antes de cada cenario, limpe o estado:

```bash
curl -X DELETE http://localhost:8080/api/state
```

### Cenario A: banco confirmou, broker falhou

```bash
curl -X POST "http://localhost:8080/api/registrations/naive/database-first?fail=true" \
  -H "Content-Type: application/json" \
  -d '{"name":"Ana","email":"ana@example.com"}'

curl http://localhost:8080/api/state
```

A chamada retorna `503`, mas o estado mostra um usuario e nenhuma mensagem.

### Cenario B: broker confirmou, banco fez rollback

```bash
curl -X DELETE http://localhost:8080/api/state

curl -X POST "http://localhost:8080/api/registrations/naive/message-first?fail=true" \
  -H "Content-Type: application/json" \
  -d '{"name":"Bruno","email":"bruno@example.com"}'

curl http://localhost:8080/api/state
```

A chamada retorna `503`; não existe usuario, mas existe uma mensagem fantasma.

### Cenario C: Outbox e retry

```bash
curl -X DELETE http://localhost:8080/api/state

curl -X POST http://localhost:8080/api/registrations/outbox \
  -H "Content-Type: application/json" \
  -d '{"name":"Carla","email":"carla@example.com"}'

# Broker fora do ar: evento continua PENDING
curl -X POST "http://localhost:8080/api/outbox/relay?failure=BEFORE_PUBLISH"
curl http://localhost:8080/api/state

# Broker voltou: retry publica e marca PROCESSED
curl -X POST "http://localhost:8080/api/outbox/relay?failure=NONE"
curl http://localhost:8080/api/state
```

### Cenario D: ACK perdido e deduplicacao

```bash
curl -X DELETE http://localhost:8080/api/state

curl -X POST http://localhost:8080/api/registrations/outbox \
  -H "Content-Type: application/json" \
  -d '{"name":"Diego","email":"diego@example.com"}'

# Mensagem chega ao broker, mas o relay perde o ACK
curl -X POST "http://localhost:8080/api/outbox/relay?failure=AFTER_PUBLISH"

# Retry com o mesmo eventId: o broker reconhece a duplicata
curl -X POST "http://localhost:8080/api/outbox/relay?failure=NONE"
curl http://localhost:8080/api/state
```

O estado final contém uma única mensagem, embora `brokerDeliveryAttempts` seja 2.

### Rollback do próprio Outbox

```bash
curl -X DELETE http://localhost:8080/api/state

curl -X POST "http://localhost:8080/api/registrations/outbox?fail=true" \
  -H "Content-Type: application/json" \
  -d '{"name":"Eva","email":"eva@example.com"}'

curl http://localhost:8080/api/state
```

Usuario e evento ficam ausentes, pois foram revertidos pela mesma transacao.

## Endpoints

| Metodo | Endpoint | Finalidade |
|---|---|---|
| `POST` | `/api/registrations/naive/database-first?fail=` | Fluxo banco primeiro |
| `POST` | `/api/registrations/naive/message-first?fail=` | Fluxo mensagem primeiro |
| `POST` | `/api/registrations/outbox?fail=` | Escrita atomica usuario + Outbox |
| `POST` | `/api/outbox/relay?failure=NONE` | Publica eventos pendentes |
| `GET` | `/api/state` | Exibe banco, Outbox e broker lado a lado |
| `DELETE` | `/api/state` | Limpa a POC |

Valores de `failure`: `NONE`, `BEFORE_PUBLISH` e `AFTER_PUBLISH`.

## Testes

```bash
mvn test
```

Os testes cobrem as duas inconsistencias, rollback atomico, retry e deduplicacao.

## Limites intencionais

- O broker em memoria deixa a POC simples; em producao seria Kafka/RabbitMQ.
- O relay e disparado por endpoint para a demonstracao ser controlavel. Em producao,
  use polling concorrente com lock/claim ou CDC (por exemplo, Debezium).
- Outbox oferece publicacao confiavel e entrega *at-least-once*, nao exatamente uma vez.
  Consumidores ainda precisam ser idempotentes.
