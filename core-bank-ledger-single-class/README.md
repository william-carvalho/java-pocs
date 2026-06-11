# Core Bank Ledger Single Class

Java 8 Spring Boot core bank ledger with one production class.

Features:

- create accounts
- deposit money
- withdraw money
- transfer money
- calculate balances from ledger entries
- keep transaction and ledger-entry history

## Run

```bash
mvn spring-boot:run
```

## Test

```bash
mvn test
```

## API

Create account:

```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d "{\"owner\":\"Ana\"}"
```

Deposit:

```bash
curl -X POST http://localhost:8080/accounts/{accountId}/deposit \
  -H "Content-Type: application/json" \
  -d "{\"amount\":100.00}"
```

Withdraw:

```bash
curl -X POST http://localhost:8080/accounts/{accountId}/withdraw \
  -H "Content-Type: application/json" \
  -d "{\"amount\":40.00}"
```

Transfer:

```bash
curl -X POST http://localhost:8080/transfers \
  -H "Content-Type: application/json" \
  -d "{\"fromAccountId\":\"A\",\"toAccountId\":\"B\",\"amount\":25.00}"
```

Balance:

```bash
curl http://localhost:8080/accounts/{accountId}/balance
```

Ledger:

```bash
curl http://localhost:8080/ledger
```
