# Hibernate Slow Query Detector

POC simples em Java 8 com Spring Boot para detectar queries lentas executadas via Hibernate/JPA.

## Ideia

A aplicacao mede o tempo de execucao de queries disparadas no fluxo JPA/Hibernate e usa `StatementInspector` para capturar o SQL final gerado. Quando a execucao ultrapassa o threshold configurado, a query e persistida no H2 como `SlowQueryRecord`.

Nao e um APM nem um detector completo para toda a JVM. E uma POC enxuta para demonstrar:

- execucao real via Hibernate/JPA
- captura do SQL
- comparacao com threshold
- persistencia do historico
- consulta por API REST

## Abordagem usada

- `StatementInspector` captura o SQL executado no contexto atual.
- `SlowQueryDetectorService` mede o tempo antes/depois da execucao JPA.
- Se o tempo for maior ou igual ao threshold, persiste um `SlowQueryRecord`.
- A demo usa H2 + uma funcao `SLEEP_MS` registrada no banco para produzir uma slow query deterministica.

## Estrutura

- `controller`: endpoints REST
- `service`: regras da POC e queries de demo
- `repository`: Spring Data JPA
- `entity`: entidades de dominio e historico
- `detector`: captura e registro de slow queries
- `config`: propriedades do threshold
- `dto`: responses da API
- `exception`: tratamento global

## Configuracao

Arquivo: [application.properties](C:/workspace/java-pocs/hibernate-slow-query-detector/src/main/resources/application.properties)

Principais propriedades:

- `app.slow-query.threshold-ms=200`
- `spring.datasource.url=jdbc:h2:mem:slowquerydb`
- `spring.h2.console.enabled=true`

## Como rodar

```bash
mvn spring-boot:run
```

Aplicacao sobe em `http://localhost:8080`.

## Fluxo de demonstracao

1. Carregar massa:

```bash
curl http://localhost:8080/demo/load-sample-data
```

2. Rodar query rapida:

```bash
curl http://localhost:8080/demo/run-fast-query
```

3. Rodar query lenta:

```bash
curl http://localhost:8080/demo/run-slow-query
```

4. Listar slow queries:

```bash
curl http://localhost:8080/slow-queries
```

5. Consultar estatisticas:

```bash
curl http://localhost:8080/slow-queries/stats
```

6. Consultar threshold:

```bash
curl http://localhost:8080/slow-queries/config
```

7. Limpar historico:

```bash
curl -X DELETE http://localhost:8080/slow-queries
```

## Endpoints

### `GET /slow-queries`

Lista slow queries detectadas.

### `GET /slow-queries/{id}`

Consulta uma slow query especifica.

### `DELETE /slow-queries`

Limpa o historico.

### `GET /slow-queries/stats`

Retorna estatisticas simples:

```json
{
  "totalDetected": 1,
  "avgExecutionTimeMs": 312.0,
  "minExecutionTimeMs": 312,
  "maxExecutionTimeMs": 312
}
```

### `GET /slow-queries/config`

Retorna o threshold atual:

```json
{
  "thresholdMs": 200
}
```

### `GET /demo/load-sample-data`

Popula `Customer` e `OrderEntity` para a demo.

### `GET /demo/run-fast-query`

Executa uma query JPA simples abaixo do threshold.

### `GET /demo/run-slow-query`

Executa uma query nativa com `SELECT SLEEP_MS(300)` para forcar uma slow query.

## Exemplo de resposta

```json
{
  "id": 1,
  "sqlText": "SELECT SLEEP_MS(?)",
  "executionTimeMs": 307,
  "thresholdMs": 200,
  "detectedAt": "2026-04-03T14:10:00",
  "queryType": "SELECT",
  "source": "demo.runSlowQuery"
}
```

## Testes

```bash
mvn test
```

Cobertura principal:

- query rapida nao registra slow query
- query lenta acima do threshold registra
- stats sao calculadas
- delete limpa historico
- endpoint de listagem responde

## Limitacoes da POC

- a medicao acontece nos fluxos embrulhados pelo `SlowQueryDetectorService`
- nao existe interceptacao global de toda query da aplicacao
- o SQL salvo e o ultimo SQL capturado no contexto atual
- nao ha agregacao por SQL nem dashboard
