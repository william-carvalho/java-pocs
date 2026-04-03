# Custom String Implementation

POC em Java 8 para uma implementacao propria de uma estrutura similar a `String`, focada em manipulacao manual sobre `char[]`. O objetivo e demonstrar algoritmos basicos de string sem delegar a logica principal para `java.lang.String`.

## O que o projeto entrega

- classe `CustomString` imutavel na pratica
- armazenamento interno em `char[]`
- iterator proprio
- metodos principais implementados manualmente
- demo de console
- testes unitarios cobrindo casos normais e de borda

## Estrutura do projeto

```text
src/main/java/com/example/customstring
|-- demo
|-- iterator
|-- model
`-- util
```

## Metodos implementados

- `toArray()`
- `forEachChar(Consumer<Character>)`
- `reverse()`
- `iterator()`
- `length()`
- `charAt(int)`
- `equals(Object)`
- `isEmpty()`
- `replace(char, char)`
- `substring(int)`
- `substring(int, int)`
- `trim()`
- `toJson()`
- `indexOf(char)`
- `hashCode()`
- `toString()`

## Por que usar char[]

O projeto usa `char[]` como estrutura interna para:

- controlar a manipulacao caractere por caractere
- implementar os algoritmos manualmente
- evitar depender dos metodos prontos de `String` para a logica principal

O construtor aceita `String` apenas por praticidade de entrada.

## Exemplo de uso

```java
CustomString text = new CustomString("hello");

text.length();                 // 5
text.charAt(1);               // 'e'
text.reverse();               // "olleh"
text.replace('l', 'x');       // "hexxo"
text.substring(1, 4);         // "ell"
text.indexOf('l');            // 2
text.toJson();                // "\"hello\""
```

## Como rodar a demo

```bash
mvn exec:java -Dexec.mainClass=com.example.customstring.demo.CustomStringDemo
```

## Como rodar os testes

```bash
mvn test
```

## Casos cobertos nos testes

- `length`
- `charAt`
- `charAt` invalido
- `reverse`
- `iterator`
- `forEachChar`
- `equals`
- `hashCode`
- `isEmpty`
- `replace`
- `substring`
- `trim`
- `toJson`
- `indexOf`
- `toArray`

## Limitacoes da POC

- `replace` implementado apenas para `char -> char`
- `indexOf` implementado apenas para `char`
- nao tenta reproduzir 100% do comportamento da `String` do JDK
- `toString()` existe apenas para facilitar debug, demo e testes
