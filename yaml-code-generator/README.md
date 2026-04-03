# YAML Code Generator

POC em Java 8 para geracao de codigo a partir de definicoes YAML. O projeto le um arquivo YAML, valida a estrutura basica, monta um modelo interno e gera classes Java simples com package, campos, construtores e getters/setters.

## O que o projeto entrega

- leitura de definicoes YAML com SnakeYAML
- validacao simples da estrutura de entrada
- modelo interno claro para `codegen`, classes e fields
- renderizacao explicita com `StringBuilder`
- escrita automatica de arquivos `.java`
- demo executavel por linha de comando
- sample YAML pronto para uso
- testes unitarios cobrindo parser, validacao, renderer e writer

## Estrutura do projeto

```text
src/main/java/com/example/yamlcodegenerator
|-- demo
|-- exception
|-- generator
|-- model
|-- parser
|-- template
|-- util
`-- writer
```

## Formato suportado do YAML

```yaml
codegen:
  basePackage: com.example.generated
  outputDir: ./generated-src
  classes:
    - name: User
      generateGettersSetters: true
      generateNoArgsConstructor: true
      generateAllArgsConstructor: true
      fields:
        - name: id
          type: Long
        - name: name
          type: String
        - name: email
          type: String
```

## Regras adotadas

- `basePackage` e obrigatorio
- `outputDir` e obrigatorio
- deve existir pelo menos uma classe
- cada classe precisa de `name`
- cada field precisa de `name` e `type`
- o gerador cria o diretorio de saida automaticamente
- sobrescrita de arquivos e permitida na POC

## O que e gerado

Para cada classe definida no YAML, o gerador cria:

- package
- declaracao da classe
- atributos privados
- construtor vazio opcional
- construtor completo opcional
- getters e setters opcionais

## Como rodar

Usando o sample YAML do projeto:

```bash
mvn exec:java -Dexec.mainClass=com.example.yamlcodegenerator.demo.YamlCodeGeneratorApplication
```

Passando um YAML especifico:

```bash
mvn exec:java -Dexec.mainClass=com.example.yamlcodegenerator.demo.YamlCodeGeneratorApplication -Dexec.args="./src/main/resources/sample-codegen.yml"
```

## Onde os arquivos sao gerados

No sample do projeto, a saida vai para:

```text
./generated-src/com/example/generated
```

Arquivos esperados:

- `User.java`
- `Address.java`

## Exemplo de uso em Java

```java
YamlDefinitionParser parser = new YamlDefinitionParser();
DefinitionValidator validator = new DefinitionValidator();
CodeGenerator codeGenerator = new CodeGenerator(
        new JavaClassGenerator(new JavaTemplateRenderer(), new FileOutputWriter()));

CodegenDefinition definition = parser.parse(Paths.get("./definitions/sample-codegen.yml"));
validator.validate(definition);
codeGenerator.generate(definition);
```

## Como estender

Os pontos mais simples para evolucao sao:

- adicionar novos tipos de artefato no modelo
- criar outro renderer alem de Java
- adicionar regras extras de validacao
- incluir geracao de `toString`, `equals/hashCode` ou `DTO`

## Rodando os testes

```bash
mvn test
```

## Arquivos principais

- `YamlDefinitionParser`: parse do YAML
- `DefinitionValidator`: validacao da definicao
- `JavaTemplateRenderer`: montagem do codigo Java
- `JavaClassGenerator`: resolucao do caminho e geracao por classe
- `FileOutputWriter`: escrita fisica dos arquivos
- `YamlCodeGeneratorApplication`: entry point da POC
