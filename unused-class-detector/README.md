# Unused Class Detector

POC em Java 8 para detectar classes potencialmente nao utilizadas em projetos Java por meio de source scanning simples. A ferramenta percorre arquivos `.java`, extrai classes declaradas, monta um grafo basico de referencias e aponta classes sem referencias de entrada.

## O que o projeto entrega

- scanner recursivo de arquivos `.java`
- parser leve de `package` e tipo top-level
- analise simples de referencias entre classes do proprio projeto
- heuristicas para ignorar classes root como `Main`, `Application` e `Config`
- relatorio de console facil de ler
- sample project pronto para demonstracao
- testes unitarios simples

## Estrutura do projeto

```text
src/main/java/com/example/unusedclassdetector
|-- analyzer
|-- config
|-- demo
|-- exception
|-- model
|-- parser
|-- report
|-- scanner
`-- util
```

## Abordagem usada

1. escaneia o diretorio informado recursivamente
2. encontra arquivos `.java`
3. extrai `package` e nome da classe/interface/enum
4. indexa as classes do projeto
5. procura referencias simples no codigo-fonte
6. monta contagem de referencias de entrada
7. sinaliza classes sem entrada como `potentially unused`

## Regras e heuristicas

- analisa apenas classes do proprio projeto
- ignora self-reference
- usa busca textual com boundary de palavra
- trata como root:
  - classes com `public static void main`
  - classes terminando com `Application`
  - classes terminando com `Main`
  - classes terminando com `Config`
  - classes em pacote `config`
  - classes que casam com `ignorePatterns`

## Limitacoes conhecidas

- analise baseada em texto simples
- reflection e dynamic loading nao sao detectados
- dependency injection e frameworks podem gerar falsos positivos
- inner classes nao sao tratadas de forma especial
- resultados devem ser lidos como `potentially unused`

## Como rodar

Usando o sample project incluso:

```bash
mvn exec:java -Dexec.mainClass=com.example.unusedclassdetector.demo.UnusedClassDetectorApplication
```

Analisando um diretorio especifico:

```bash
mvn exec:java -Dexec.mainClass=com.example.unusedclassdetector.demo.UnusedClassDetectorApplication -Dexec.args="C:/meu-projeto/src/main/java"
```

## Exemplo de saida

```text
Unused Class Detector Report
----------------------------
Source root: sample-project/src/main/java
Total classes found: 5
Referenced classes: 3
Ignored classes: 1
Potentially unused classes: 1

Potentially unused:
- com.example.sample.UnusedHelper
```

## Sample project

O projeto inclui um mini exemplo em:

```text
sample-project/src/main/java
```

Classes:

- `AppMain`
- `UserController`
- `UserService`
- `UserRepository`
- `UnusedHelper`

Nesse cenario, `UnusedHelper` aparece como potencialmente nao utilizada.

## Como configurar ignores

O demo usa um `DetectorConfig` com:

- `sourceRoot`
- `ignorePatterns`
- `includeTests`

Exemplo:

```java
DetectorConfig config = new DetectorConfig(
        Paths.get("src/main/java"),
        Arrays.asList(".*Test.*", ".*Mapper"),
        false
);
```

## Testes

```bash
mvn test
```

Os testes cobrem:

- scanner recursivo
- parser de package e className
- deteccao de referencias
- ignorar self-reference
- deteccao de classe unused
- ignore pattern como root
