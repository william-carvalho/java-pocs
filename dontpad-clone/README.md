# DontPad Clone

POC em Java 8 com Spring Boot inspirada no DontPad. O projeto entrega um pad de texto compartilhavel por URL, com criacao automatica por slug, API REST simples e uma UI minima para editar e salvar o conteudo no navegador.

## O que o projeto entrega

- pad por slug em URL
- criacao automatica ao abrir um slug inexistente
- persistencia em H2
- API REST para criar, buscar, atualizar, listar e deletar
- pagina HTML simples com textarea e botao Save
- validacao simples de slug

## Estrutura do projeto

```text
src/main/java/com/example/dontpadclone
|-- config
|-- controller
|-- dto
|-- entity
|-- exception
|-- repository
`-- service
```

## Regras adotadas

- slug e unico
- slug deve seguir o padrao `[a-zA-Z0-9_-]+`
- content pode ser vazio
- `GET /api/pads/{slug}` cria automaticamente se nao existir
- `PUT /api/pads/{slug}` cria ou atualiza no mesmo endpoint
- `DELETE /api/pads/{slug}` retorna erro claro se o pad nao existir

## Como rodar

```bash
mvn spring-boot:run
```

Aplicacao sobe em:

```text
http://localhost:8080
```

H2 console:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:dontpaddb
```

## UI minima

Abra um pad direto no navegador:

```text
http://localhost:8080/pads/minha-anotacao
```

Se o pad nao existir, ele e criado automaticamente com conteudo vazio.

Tambem existe uma home simples em:

```text
http://localhost:8080/
```

## Endpoints REST

### POST /api/pads

Payload:

```json
{
  "slug": "minha-anotacao",
  "content": "Ola mundo"
}
```

### GET /api/pads/{slug}

Busca o pad por slug. Se nao existir, cria automaticamente e retorna.

### PUT /api/pads/{slug}

Payload:

```json
{
  "content": "Conteudo atualizado"
}
```

### GET /api/pads

Lista pads resumidos.

### DELETE /api/pads/{slug}

Remove o pad pelo slug.

## Exemplos de curl

Criar pad:

```bash
curl -X POST http://localhost:8080/api/pads ^
  -H "Content-Type: application/json" ^
  -d "{\"slug\":\"minha-anotacao\",\"content\":\"Ola mundo\"}"
```

Buscar pad:

```bash
curl http://localhost:8080/api/pads/minha-anotacao
```

Atualizar pad:

```bash
curl -X PUT http://localhost:8080/api/pads/minha-anotacao ^
  -H "Content-Type: application/json" ^
  -d "{\"content\":\"Conteudo atualizado\"}"
```

Listar pads:

```bash
curl http://localhost:8080/api/pads
```

Deletar pad:

```bash
curl -X DELETE http://localhost:8080/api/pads/minha-anotacao
```

## Fluxo demonstravel

1. Abrir `/pads/minha-anotacao`
2. Editar o texto no textarea
3. Clicar em `Save`
4. Recarregar a pagina
5. Ver o conteudo persistido

## Seed inicial

Ao subir a aplicacao, um pad `welcome` e criado automaticamente com:

```text
Welcome to DontPad Clone
```

## Testes

```bash
mvn test
```

Os testes cobrem:

- criacao explicita
- criacao automatica por slug
- update por slug
- delete
- validacao de slug invalido
