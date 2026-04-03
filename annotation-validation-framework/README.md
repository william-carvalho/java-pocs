# Annotation Validation Framework

POC em Java 8 para validacao baseada em annotations proprias, usando reflection de forma simples e controlada. A proposta e entregar um mini framework estilo Bean Validation, mas manual, pequeno e facil de estender.

## O que o projeto entrega

- annotations customizadas para validacao de campos
- validators explicitos por annotation
- engine generica para validar qualquer objeto anotado
- resultado estruturado com lista de erros
- demo em console com casos validos e invalidos
- testes unitarios cobrindo os cenarios principais

## Annotations suportadas

- `@NotNull`
- `@NotBlank`
- `@Size(min, max)`
- `@Min(value)`
- `@Max(value)`
- `@Email`
- `@Pattern(regex)`

## Estrutura do projeto

```text
src/main/java/com/example/validationframework
|-- annotation
|-- demo
|-- engine
|-- exception
|-- model
|-- util
`-- validator
```

## Arquitetura simples

- `annotation`: annotations customizadas do framework
- `validator`: validators concretos por annotation
- `engine`: registry de validators e engine principal
- `model`: `ValidationResult` e `ValidationError`
- `demo`: classes anotadas e `main` de demonstracao
- `util`: impressao simples do resultado no console

## Como usar

### 1. Criar a engine

```java
ValidatorEngine validatorEngine =
        new DefaultValidatorEngine(ValidatorRegistry.withDefaults());
```

### 2. Anotar sua classe

```java
public class UserRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email")
    private String email;

    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must be at most 100")
    private Integer age;
}
```

### 3. Validar um objeto

```java
UserRequest request = new UserRequest("", "invalid-email", "123", 15);
ValidationResult result = validatorEngine.validate(request);

if (!result.isValid()) {
    result.getErrors().forEach(System.out::println);
}
```

## Como registrar uma nova annotation

1. Crie a annotation em `annotation`
2. Crie um validator implementando `FieldValidator<A>`
3. Registre no `ValidatorRegistry`

Exemplo:

```java
registry.register(MyAnnotation.class, new MyAnnotationValidator());
```

## Regras adotadas na POC

- o framework acumula todos os erros e nao para no primeiro
- campos sem annotations conhecidas sao ignorados
- objeto `null` retorna `ValidationResult` invalido com erro claro
- `@Size`, `@Min`, `@Max`, `@Email` e `@Pattern` ignoram `null`
- obrigatoriedade fica a cargo de `@NotNull` e `@NotBlank`

## Rodando a demo

```bash
mvn exec:java -Dexec.mainClass=com.example.validationframework.demo.ValidationFrameworkDemo
```

## Rodando os testes

```bash
mvn test
```

## Fluxo da demo

A demo:

- valida um `UserRequest` invalido
- valida um `UserRequest` valido
- valida um `ProductRequest` invalido
- valida um `AddressRequest` valido
- imprime os erros estruturados no console

## Observacoes

O framework foi mantido propositalmente pequeno. Nao usa Hibernate Validator, Jakarta Validation nem reflection "magica" demais. O foco e mostrar uma base funcional, clara e facil de evoluir.
