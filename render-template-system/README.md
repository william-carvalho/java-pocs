# Render Template System

POC em Java 8 com Spring Boot para renderizar o mesmo template em HTML, PDF ou CSV usando uma unica API.

## Stack

- Java 8
- Spring Boot 2.7
- Maven
- OpenPDF

## O que a POC entrega

- Endpoint unico para renderizacao
- Template inicial `invoice`
- Suporte a `HTML`, `PDF` e `CSV`
- Mesmo conjunto de dados para os tres formatos
- Tratamento claro para template inexistente, formato invalido e dados obrigatorios ausentes
- Estrutura simples e facil de extender

## Estrutura

```text
src/main/java/com/example/rendertemplatesystem
|-- controller
|-- service
|-- renderer
|-- template
|-- dto
|-- exception
|-- config
`-- enums
```

## Arquitetura rapida

- `RenderController`: expoe os endpoints REST
- `RenderService`: valida request e orquestra o fluxo
- `RendererResolver`: escolhe o renderer correto pelo formato
- `TemplateRenderer`: contrato comum dos renderizadores
- `HtmlTemplateRenderer`: retorna HTML simples
- `PdfTemplateRenderer`: gera PDF usando OpenPDF
- `CsvTemplateRenderer`: gera CSV em memoria
- `TemplateProvider`: guarda templates cadastrados
- `GlobalExceptionHandler`: centraliza respostas de erro

## Template inicial

Template disponivel: `invoice`

Campos esperados:

- `customerName`
- `documentNumber`
- `amount`
- `dueDate`

## Como rodar

```bash
cd render-template-system
mvn spring-boot:run
```

Aplicacao em `http://localhost:8080`.

## Endpoints

### GET /templates

Lista os templates disponiveis.

```bash
curl http://localhost:8080/templates
```

### GET /formats

Lista os formatos suportados.

```bash
curl http://localhost:8080/formats
```

### POST /render

Renderiza o template no formato pedido.

```bash
curl -X POST http://localhost:8080/render \
  -H "Content-Type: application/json" \
  -d '{
    "templateName": "invoice",
    "format": "HTML",
    "data": {
      "customerName": "William Carvalho",
      "documentNumber": "INV-2026-001",
      "amount": "1500.00",
      "dueDate": "2026-04-10"
    }
  }'
```

### Exemplo HTML

```bash
curl -X POST http://localhost:8080/render \
  -H "Content-Type: application/json" \
  -d '{
    "templateName": "invoice",
    "format": "HTML",
    "data": {
      "customerName": "William Carvalho",
      "documentNumber": "INV-2026-001",
      "amount": "1500.00",
      "dueDate": "2026-04-10"
    }
  }'
```

Resposta com `Content-Type: text/html`.

### Exemplo PDF

```bash
curl -X POST http://localhost:8080/render \
  -H "Content-Type: application/json" \
  -d '{
    "templateName": "invoice",
    "format": "PDF",
    "data": {
      "customerName": "William Carvalho",
      "documentNumber": "INV-2026-001",
      "amount": "1500.00",
      "dueDate": "2026-04-10"
    }
  }' --output invoice.pdf
```

Resposta com `Content-Type: application/pdf`.

### Exemplo CSV

```bash
curl -X POST http://localhost:8080/render \
  -H "Content-Type: application/json" \
  -d '{
    "templateName": "invoice",
    "format": "CSV",
    "data": {
      "customerName": "William Carvalho",
      "documentNumber": "INV-2026-001",
      "amount": "1500.00",
      "dueDate": "2026-04-10"
    }
  }'
```

Resposta esperada:

```text
customerName,documentNumber,amount,dueDate
William Carvalho,INV-2026-001,1500.00,2026-04-10
```

## Exemplo de erro para template inexistente

```json
{
  "status": 404,
  "error": "Template not found",
  "message": "Template not found: unknown",
  "path": "/render",
  "timestamp": "2026-04-03T18:00:00Z"
}
```

## Exemplo de erro para formato invalido

```json
{
  "status": 400,
  "error": "Invalid request body",
  "message": "Invalid format. Supported values: HTML, PDF, CSV",
  "path": "/render",
  "timestamp": "2026-04-03T18:00:00Z"
}
```

## Como extender

1. Criar um novo valor em `RenderFormat`
2. Implementar `TemplateRenderer`
3. Registrar o renderer como bean Spring

O endpoint continua o mesmo.
