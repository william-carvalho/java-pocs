# Core Bank Ledger

POC em Java 8 com Spring Boot para um core bancario simples focado em ledger. O projeto cobre criacao de contas, saldo, credito, debito, transferencia, historico auditavel e consistencia transacional com `@Transactional`.

## O que o projeto entrega

- criacao de contas com numero sequencial simples
- saldo atual armazenado em `Account.currentBalance`
- historico auditavel em `LedgerTransaction` e `LedgerEntry`
- credito, debito e transferencia entre contas
- bloqueio de movimentacao para contas `BLOCKED` e `CLOSED`
- validacao de saldo insuficiente
- H2 em memoria
- seed inicial com 2 contas

## Estrutura do projeto

```text
src/main/java/com/example/corebankledger
|-- config
|-- controller
|-- dto
|-- entity
|-- exception
|-- repository
`-- service
```

## Modelo adotado

### Account

- `id`
- `accountNumber`
- `ownerName`
- `type`
- `status`
- `currentBalance`
- `createdAt`

### LedgerTransaction

- `id`
- `transactionReference`
- `type`
- `description`
- `createdAt`

### LedgerEntry

- `id`
- `transaction`
- `account`
- `entryType`
- `amount`
- `balanceAfter`
- `createdAt`

## Regras principais

- conta nasce com saldo `0.00`
- valores monetarios usam `BigDecimal`
- `amount` deve ser maior que zero
- debito e transferencia nao permitem saldo negativo
- conta precisa estar `ACTIVE` para movimentar
- transferencia gera 2 entries na mesma transacao
- operacoes financeiras sao atomicas com `@Transactional`

## Como rodar

```bash
mvn spring-boot:run
```

Aplicacao sobe em `http://localhost:8080`.

H2 console:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:corebankledgerdb
```

## Endpoints

### POST /accounts

Payload:

```json
{
  "ownerName": "William Carvalho",
  "type": "CHECKING"
}
```

### GET /accounts

### GET /accounts/{id}

### GET /accounts/{id}/balance

### PATCH /accounts/{id}/status

Payload:

```json
{
  "status": "BLOCKED"
}
```

### GET /accounts/{id}/entries

### POST /transactions/credit

Payload:

```json
{
  "accountId": 1,
  "amount": 1000.00,
  "description": "Initial deposit"
}
```

### POST /transactions/debit

Payload:

```json
{
  "accountId": 1,
  "amount": 100.00,
  "description": "Purchase"
}
```

### POST /transactions/transfer

Payload:

```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 250.00,
  "description": "Transfer to savings"
}
```

### GET /transactions

Filtros opcionais:

- `accountId`
- `type`

Exemplo:

```text
/transactions?accountId=1&type=TRANSFER
```

### GET /transactions/{id}

## Exemplos de curl

Criar conta:

```bash
curl -X POST http://localhost:8080/accounts ^
  -H "Content-Type: application/json" ^
  -d "{\"ownerName\":\"William Carvalho\",\"type\":\"CHECKING\"}"
```

Listar contas:

```bash
curl http://localhost:8080/accounts
```

Consultar saldo:

```bash
curl http://localhost:8080/accounts/1/balance
```

Creditar:

```bash
curl -X POST http://localhost:8080/transactions/credit ^
  -H "Content-Type: application/json" ^
  -d "{\"accountId\":1,\"amount\":1000.00,\"description\":\"Initial deposit\"}"
```

Debitar:

```bash
curl -X POST http://localhost:8080/transactions/debit ^
  -H "Content-Type: application/json" ^
  -d "{\"accountId\":1,\"amount\":100.00,\"description\":\"Purchase\"}"
```

Transferir:

```bash
curl -X POST http://localhost:8080/transactions/transfer ^
  -H "Content-Type: application/json" ^
  -d "{\"fromAccountId\":1,\"toAccountId\":2,\"amount\":250.00,\"description\":\"Transfer to savings\"}"
```

Consultar entries:

```bash
curl http://localhost:8080/accounts/1/entries
```

## Fluxo demonstravel

1. Criar conta A
2. Criar conta B
3. Creditar 1000 na conta A
4. Transferir 300 da conta A para B
5. Debitar 100 da conta B
6. Consultar saldos
7. Consultar entries das duas contas

Resultado esperado:

- saldo A = `700.00`
- saldo B = `200.00`

## Testes

```bash
mvn test
```

Os testes cobrem:

- fluxo de credito + transferencia + debito
- saldo insuficiente
- conta bloqueada sem movimentacao
