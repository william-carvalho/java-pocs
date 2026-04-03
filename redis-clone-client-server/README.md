# Redis Clone Client/Server

POC simples em Java 8 com sockets TCP para simular um clone enxuto de Redis, com suporte a operações básicas de `Strings` e `Maps`.

## Stack

- Java 8
- Maven
- Sockets TCP
- `ConcurrentHashMap` para storage em memória

## Escopo da POC

- Servidor TCP simples
- Cliente TCP simples
- Protocolo texto linha a linha
- Dados somente em memória
- Suporte a múltiplos clientes com `ExecutorService`

Não faz parte desta POC:

- persistência
- TTL
- pub/sub
- cluster
- replicação
- autenticação
- transações

## Estrutura

```text
src/main/java/com/example/redisclone
├── client
├── command
├── exception
├── handler
├── protocol
├── server
├── storage
└── util
```

## Regras adotadas

- Valores com espaço não são suportados para manter o protocolo simples.
- `APPEND_STRING` cria a chave se ela ainda não existir.
- `MAP_KEYS` e `MAP_VALUES` retornam `EMPTY` quando o mapa não existe ou está vazio.
- `QUIT` encerra a conexão do cliente de forma graciosa.

## Como compilar

```bash
mvn clean package
```

## Como subir o servidor

Porta padrão: `6379`

```bash
mvn exec:java -Dexec.mainClass=com.example.redisclone.server.RedisCloneServerApplication
```

Subindo em outra porta:

```bash
mvn exec:java -Dexec.mainClass=com.example.redisclone.server.RedisCloneServerApplication -Dexec.args="6380"
```

Saída esperada:

```text
Redis clone server listening on port 6379
```

## Como subir o cliente

Host padrão: `localhost`

Porta padrão: `6379`

```bash
mvn exec:java -Dexec.mainClass=com.example.redisclone.client.RedisCloneClientApplication
```

Conectando em outro host/porta:

```bash
mvn exec:java -Dexec.mainClass=com.example.redisclone.client.RedisCloneClientApplication -Dexec.args="localhost 6380"
```

Saída esperada:

```text
Connected to localhost:6379
Type commands. Use QUIT to close the client.
```

## Comandos suportados

### Strings

- `SET_STRING key value`
- `GET_STRING key`
- `REMOVE_STRING key`
- `APPEND_STRING key suffix`

### Maps

- `MAP_SET mapName field value`
- `MAP_GET mapName field`
- `MAP_KEYS mapName`
- `MAP_VALUES mapName`

### Conexão

- `QUIT`

## Exemplos reais de uso

```text
> SET_STRING city Floripa
OK

> GET_STRING city
VALUE Floripa

> APPEND_STRING city SC
VALUE FloripaSC

> REMOVE_STRING city
REMOVED

> GET_STRING city
NOT_FOUND
```

```text
> MAP_SET person name William
OK

> MAP_SET person role CTO
OK

> MAP_GET person name
VALUE William

> MAP_KEYS person
KEYS [name, role]

> MAP_VALUES person
VALUES [William, CTO]
```

## Erros esperados

Comando inválido:

```text
> SOMETHING test
ERROR Unknown command
```

Argumentos insuficientes:

```text
> SET_STRING onlyKey
ERROR Invalid arguments
```

## Testando com dois clientes ao mesmo tempo

1. Suba o servidor.
2. Abra dois terminais separados.
3. Inicie um cliente em cada terminal.
4. No cliente 1:

```text
SET_STRING shared hello
```

5. No cliente 2:

```text
GET_STRING shared
```

Você deve ver:

```text
VALUE hello
```

Outro exemplo com mapa:

Cliente 1:

```text
MAP_SET session user William
```

Cliente 2:

```text
MAP_GET session user
```

Resposta:

```text
VALUE William
```

## Testes

```bash
mvn test
```
