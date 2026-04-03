# HTTP Stress Test Framework

POC em Java 8 para stress test HTTP com foco em concorrencia, latencia e consolidacao de metricas. O projeto usa `HttpURLConnection`, `ExecutorService` e um relatorio textual simples no console.

## O que o projeto entrega

- configuracao simples de teste HTTP
- suporte a `GET` e `POST`
- execucao concorrente com pool proprio
- medicao de sucesso, falha, status code e latencia
- consolidacao de metricas finais
- percentis simples `p50` e `p95`
- demo pronta para rodar
- testes unitarios e integracao leve

## Estrutura do projeto

```text
src/main/java/com/example/httpstresstest
|-- config
|-- core
|-- demo
|-- exception
|-- executor
|-- http
|-- metric
|-- model
|-- report
`-- util
```

## Arquitetura simples

- `StressTestConfig`: define URL, metodo, volume, concorrencia e timeouts
- `StressTestRunner`: executa o teste concorrente
- `HttpRequestExecutor`: faz a request HTTP com `HttpURLConnection`
- `MetricsCollector`: agrega resultados em memoria
- `StressTestReport`: representa o resumo final
- `ConsoleReportPrinter`: imprime o relatorio

## Metricas suportadas

- total de requests
- requests com sucesso
- requests com falha
- success rate
- min latency
- max latency
- avg latency
- p50 latency
- p95 latency
- total duration
- requests per second
- contagem por status code

## Exemplo de uso em Java

```java
StressTestConfig config = StressTestConfig.builder()
        .url("http://localhost:8080/health")
        .method(HttpMethod.GET)
        .totalRequests(100)
        .threadPoolSize(10)
        .connectTimeoutMs(3000)
        .readTimeoutMs(3000)
        .build();

StressTestReport report = new StressTestRunner().run(config);
ConsoleReportPrinter.print(report);
```

## Como rodar a demo

Sem argumentos:

```bash
mvn exec:java -Dexec.mainClass=com.example.httpstresstest.demo.HttpStressTestApplication
```

Nesse modo, a demo sobe um servidor HTTP local leve na porta `9091` e executa:

- um cenario de sucesso em `/health`
- um cenario de falha em `/unknown`

Com argumentos:

```bash
mvn exec:java -Dexec.mainClass=com.example.httpstresstest.demo.HttpStressTestApplication -Dexec.args="http://localhost:8080/health GET 500 20"
```

Parametros:

1. `url`
2. `method`
3. `totalRequests`
4. `threadPoolSize`

## Exemplo de saida

```text
Stress Test Report
-------------------
URL: http://localhost:9091/health
Method: GET
Total Requests: 100
Successful Requests: 100
Failed Requests: 0
Success Rate: 100.00%
Min Latency: 1 ms
Max Latency: 25 ms
Avg Latency: 4.32 ms
P50 Latency: 3 ms
P95 Latency: 9 ms
Total Duration: 180 ms
Requests/sec: 555.55
Status Codes:
200 -> 100
```

## Regras da POC

- `url` e obrigatoria
- `method` e obrigatorio
- `totalRequests` deve ser maior que zero
- `threadPoolSize` deve ser maior que zero
- timeouts devem ser validos
- status `2xx` contam como sucesso
- status fora de `2xx` contam como falha, mas entram no relatorio
- excecao de request tambem conta como falha
- a execucao continua mesmo com falhas individuais

## Limitacoes

- sem execucao distribuida
- sem UI
- sem dashboard
- sem ramp-up sofisticado
- sem stop-on-error
- sem suporte avancado a cookies, multipart ou autenticacao

## Testes

```bash
mvn test
```

Os testes cobrem:

- validacao da configuracao
- calculo de metricas e percentis
- consolidacao de falhas por status code
- execucao de integracao contra servidor HTTP local simples
