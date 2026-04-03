# HTTP Server GET Endpoints

POC em Java 8 para um servidor HTTP simples implementado manualmente com sockets TCP. O foco e demonstrar parsing basico de request HTTP, roteamento de endpoints GET e escrita de respostas HTTP validas sem Spring Boot, Tomcat ou Jetty.

## O que o projeto entrega

- servidor HTTP simples com `ServerSocket`
- parsing manual da request line e headers basicos
- roteador proprio para endpoints GET
- suporte a query params
- respostas HTTP com status line, headers e body
- tratamento basico de `400`, `404`, `405` e `500`
- suporte simples a multiplos clientes com `ExecutorService`

## Estrutura do projeto

```text
src/main/java/com/example/httpserver
|-- demo
|-- exception
|-- handler
|-- http
|-- model
|-- router
|-- server
`-- util
```

## Endpoints disponiveis

- `GET /` -> `HTTP Server is running`
- `GET /health` -> `{"status":"UP"}`
- `GET /hello` -> `Hello, World`
- `GET /hello?name=William` -> `Hello, William`
- `GET /time` -> horario atual do servidor
- `GET /echo?foo=bar&x=1` -> `{"foo":"bar","x":"1"}`

## Como rodar

Na porta padrao `8080`:

```bash
mvn exec:java -Dexec.mainClass=com.example.httpserver.demo.HttpServerApplication
```

Em outra porta:

```bash
mvn exec:java -Dexec.mainClass=com.example.httpserver.demo.HttpServerApplication -Dexec.args="9090"
```

## Exemplos de curl

Raiz:

```bash
curl http://localhost:8080/
```

Health:

```bash
curl http://localhost:8080/health
```

Hello:

```bash
curl http://localhost:8080/hello
```

Hello com nome:

```bash
curl "http://localhost:8080/hello?name=William"
```

Echo:

```bash
curl "http://localhost:8080/echo?foo=bar&x=1"
```

Path invalido:

```bash
curl http://localhost:8080/unknown
```

Metodo invalido:

```bash
curl -X POST http://localhost:8080/
```

## Arquitetura simples

- `SimpleHttpServer`: aceita conexoes TCP
- `ClientConnectionHandler`: processa uma conexao por vez
- `HttpRequestParser`: extrai metodo, path, versao e headers
- `HttpRouter`: resolve `GET + path`
- `RouteHandler`: interface dos handlers
- `HttpResponseWriter`: monta e envia a resposta HTTP

## Regras da POC

- suporta apenas `GET`
- outros metodos retornam `405 Method Not Allowed`
- path desconhecido retorna `404 Not Found`
- request malformada retorna `400 Bad Request`
- erro inesperado retorna `500 Internal Server Error`
- a conexao e fechada apos cada resposta

## Limitacoes

- nao suporta POST, PUT, DELETE ou body parsing
- nao suporta keep-alive
- nao suporta HTTPS
- nao e um parser HTTP completo
- nao usa servlet container nem framework web

## Testes

```bash
mvn test
```

Os testes cobrem:

- parser de method/path/query
- router para rota existente
- `404` para rota inexistente
- `405` para metodo invalido
- montagem da resposta HTTP
- parsing de query params
