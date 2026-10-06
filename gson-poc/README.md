# Gson POC

Projeto Maven em Java 8 que demonstra serializacao e desserializacao JSON com
[Gson](https://github.com/google/gson).

## O que a POC demonstra

- objeto Java para JSON e JSON para objeto Java;
- `@SerializedName` para mapear nomes diferentes;
- omissao de campo `transient` e inclusao de valores `null`;
- adapter para tipos de `java.time`, como `LocalDate`;
- colecoes e wrappers genericos com `TypeToken`;
- leitura e alteracao da arvore JSON com `JsonObject`;
- `TypeAdapter` customizado para representar `Money` como `"BRL 1500.00"`;
- parser configurado no modo estrito;
- JSON legivel com pretty printing.

## Estrutura

```text
src/main/java/com/example/gson/
|-- GsonPocApplication.java   # demonstracao executavel
|-- GsonFactory.java          # configuracao central do Gson
|-- adapter/                   # adapters de Money e LocalDate
`-- model/                    # objetos usados nos exemplos
```

## Executar

Requisitos: JDK 8+ e Maven 3.8+.

```bash
cd gson-poc
mvn clean test
mvn exec:java
```

A aplicacao imprime no terminal os resultados de cada exemplo. Os testes validam
o round trip, tipos genericos, adapter customizado, campos nulos/transient e JSON
malformado.

## Ponto importante sobre genericos

Por causa do type erasure do Java, nao use apenas `List.class`: isso produz uma
lista de mapas, sem preservar `Customer`. Informe o tipo completo:

```java
Type type = new TypeToken<List<Customer>>() { }.getType();
List<Customer> customers = gson.fromJson(json, type);
```
