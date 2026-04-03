# Observability Latency Framework

POC em Java 8 para observabilidade focada em metricas de latencia. O projeto mede blocos de codigo, agrega estatisticas em memoria e expoe uma API Java pequena para consulta e reset.

## O que o projeto entrega

- registro manual de latencia por nome de operacao
- medicao automatica com `Runnable` e `Supplier<T>`
- agregacao em memoria com estruturas thread-safe
- estatisticas por operacao: count, min, max, avg, total, last
- `errorCount` simples quando a operacao falha
- consulta de uma metrica especifica ou de todas
- reset por operacao e reset global
- demo console e testes unitarios

## Estrutura do projeto

```text
src/main/java/com/example/observabilitylatencyframework
|-- collector
|-- core
|-- demo
|-- exception
|-- metric
|-- model
|-- registry
|-- timer
`-- util
```

## Arquitetura simples

- `core`: fachada principal do framework
- `registry`: mapa central `operationName -> metric accumulator`
- `collector`: atualizacao das metricas agregadas
- `timer`: helper para medir tempo com `System.nanoTime()`
- `metric`: snapshot imutavel retornado ao consumidor
- `exception`: erros claros para operacao invalida ou inexistente
- `demo`: `main` pronta para demonstracao

## API principal

```java
LatencyFramework framework = new DefaultLatencyFramework();

framework.record("db.query.users", 120);

String user = framework.measure("user-service.findUser", () -> {
    Thread.sleep(100);
    return "William";
});

framework.measure("payment.process", () -> {
    Thread.sleep(200);
});

LatencyMetric metric = framework.getMetric("user-service.findUser");
```

## Regras adotadas

- `operationName` e obrigatorio
- latencia deve ser maior ou igual a zero
- operacoes novas sao criadas automaticamente no primeiro registro
- `measure(...)` registra a latencia mesmo quando a operacao falha
- quando ha erro durante `measure(...)`, a exception e relancada
- `errorCount` incrementa apenas nas execucoes com falha

## Exemplo de consulta

Uma metrica retorna:

- `operationName`
- `count`
- `minLatencyMs`
- `maxLatencyMs`
- `avgLatencyMs`
- `totalLatencyMs`
- `lastLatencyMs`
- `errorCount`

## Rodando a demo

```bash
mvn exec:java -Dexec.mainClass=com.example.observabilitylatencyframework.demo.LatencyFrameworkDemo
```

## Rodando os testes

```bash
mvn test
```

## Fluxo da demo

A demo:

- registra metricas manualmente
- mede operacoes com `sleep` para simular latencia
- consulta uma metrica especifica
- lista todas as metricas
- simula uma falha em `external.api.call`
- executa `reset` e `resetAll`

## Extensao simples

Se quiser evoluir a POC depois, os pontos mais naturais sao:

- percentis simples como `p50` e `p95`
- ranking das operacoes mais lentas
- endpoint REST de consulta
- export basico em JSON
