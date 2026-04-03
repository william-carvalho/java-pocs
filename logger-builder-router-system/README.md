# Logger Builder Router System

POC em Java 8 com Spring Boot para publicar logs usando uma API unica, com roteamento por destino e execucao sincronica ou assincrona.

## Stack

- Java 8
- Spring Boot 2.7
- Maven

## O que a POC entrega

- API REST unica para publicacao de logs
- Destinos iniciais: `FS`, `ELK`, `CUSTOM`
- Modo `SYNC` e `ASYNC` usando o mesmo endpoint
- Escrita real em arquivo local para `FS`
- Simulacao estruturada para `ELK`
- Destino generico e extensivel para `CUSTOM`
- Tratamento centralizado para validacao e erros

## Estrutura

```text
src/main/java/com/example/loggerbuilderroutersystem
|-- config
|-- controller
|-- dto
|-- enums
|-- exception
|-- router
|-- service
`-- strategy
```

## Arquitetura rapida

- `LogController`: expoe os endpoints REST
- `LogService`: controla o fluxo sync e async
- `LogRouter`: resolve o writer correto por destino
- `LogWriter`: contrato unico dos destinos
- `FileSystemLogWriter`: grava no arquivo local
- `ElkLogWriter`: simula um payload estruturado
- `CustomLogWriter`: exemplo de destino generico
- `GlobalExceptionHandler`: centraliza respostas de erro

Sem camadas extras e sem abstracoes desnecessarias. O foco e demonstrar funcionamento e extensibilidade com codigo simples.

## Como rodar

```bash
cd logger-builder-router-system
mvn spring-boot:run
```

A aplicacao sobe em `http://localhost:8080`.

## Como testar

### Destinos disponiveis

```bash
curl http://localhost:8080/logs/destinations
```

### Modos disponiveis

```bash
curl http://localhost:8080/logs/modes
```

### Log em arquivo com processamento sincrono

```bash
curl -X POST http://localhost:8080/logs \
  -H "Content-Type: application/json" \
  -d '{
    "message": "User created successfully",
    "level": "INFO",
    "destination": "FS",
    "mode": "SYNC",
    "metadata": {
      "userId": "123",
      "module": "user-service"
    }
  }'
```

### Log ELK fake com processamento assincrono

```bash
curl -X POST http://localhost:8080/logs \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Payment integration timeout",
    "level": "ERROR",
    "destination": "ELK",
    "mode": "ASYNC",
    "metadata": {
      "transactionId": "TX999",
      "module": "payment"
    }
  }'
```

### Log em destino custom

```bash
curl -X POST http://localhost:8080/logs \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Custom integration event",
    "level": "DEBUG",
    "destination": "CUSTOM",
    "mode": "SYNC",
    "metadata": {
      "source": "backoffice"
    }
  }'
```

## Exemplos de resposta

### Sucesso sync

```json
{
  "status": "SUCCESS",
  "message": "Log processed successfully",
  "destination": "FS",
  "mode": "SYNC",
  "processedAt": "2026-04-03T17:00:00Z",
  "acceptedAt": null
}
```

### Sucesso async

```json
{
  "status": "ACCEPTED",
  "message": "Log accepted for asynchronous processing",
  "destination": "ELK",
  "mode": "ASYNC",
  "processedAt": null,
  "acceptedAt": "2026-04-03T17:00:00Z"
}
```

### Erro de destination invalido

```json
{
  "status": 400,
  "error": "Invalid request body",
  "message": "Invalid destination. Supported values: FS, ELK, CUSTOM",
  "path": "/logs",
  "timestamp": "2026-04-03T17:00:00Z"
}
```

### Erro de mode invalido

```json
{
  "status": 400,
  "error": "Invalid request body",
  "message": "Invalid mode. Supported values: SYNC, ASYNC",
  "path": "/logs",
  "timestamp": "2026-04-03T17:00:00Z"
}
```

## Arquivo de log local

Por padrao, o log em arquivo fica em `logs/app.log`. Se a pasta ou o arquivo nao existirem, a aplicacao cria automaticamente.

## Como estender

1. Criar um novo `LogDestination`
2. Implementar `LogWriter`
3. Registrar a nova implementacao como bean Spring

O endpoint `/logs` continua o mesmo.
