# Converter Framework

POC simples em Java 8 para conversão de tipos complexos com registry manual, serviço central de conversão, nested conversion e suporte a listas.

## Ideia

O framework resolve um `Converter` pelo par origem/destino e executa a conversão de forma explícita e extensível.

Ele cobre:

- conversão de objeto único
- conversão de lista
- nested object
- campo com nome diferente
- campo derivado
- erro claro quando o converter não existe

## Estrutura

```text
src/main/java/com/example/converterframework
├── core
├── converter
├── demo
├── exception
├── model
│   ├── dto
│   └── entity
├── registry
├── service
└── util
```

## Componentes principais

- `Converter<S, T>`: contrato base dos conversores
- `ConverterKey`: chave origem/destino
- `ConverterRegistry`: registro central dos conversores
- `ConversionService`: facade principal
- `DefaultConversionService`: implementação padrão
- `ConverterNotFoundException`: erro claro para conversão inexistente

## Conversores implementados

- `AddressEntity -> AddressDTO`
- `UserEntity -> UserDTO`
- `ProductEntity -> ProductDTO`
- `OrderEntity -> OrderResponse`

## Exemplos de conversão

### Nested object

- `UserEntity.address -> AddressDTO`
- `UserEntity.name -> UserDTO.fullName`

### Campo derivado

- `OrderEntity.customer.name -> OrderResponse.customerName`
- `OrderEntity.items.size() -> OrderResponse.itemCount`

## Como registrar converters

```java
ConverterRegistry registry = new ConverterRegistry();
ConversionService conversionService = new DefaultConversionService(registry);

registry.register(AddressEntity.class, AddressDTO.class, new AddressEntityToAddressDTOConverter());
registry.register(UserEntity.class, UserDTO.class, new UserEntityToUserDTOConverter());
registry.register(ProductEntity.class, ProductDTO.class, new ProductEntityToProductDTOConverter());
registry.register(OrderEntity.class, OrderResponse.class, new OrderEntityToOrderResponseConverter());
```

## Como converter um objeto

```java
AddressEntity address = new AddressEntity("Street 1", "Floripa", "88000-000");
UserEntity user = new UserEntity(1L, "William Carvalho", "william@email.com", address);

UserDTO dto = conversionService.convert(user, UserDTO.class);
```

## Como converter uma lista

```java
List<ProductDTO> response = conversionService.convertList(products, ProductDTO.class);
```

## Como estender o framework

1. Crie um novo `Converter<S, T>`.
2. Registre no `ConverterRegistry`.
3. Use `conversionService.convert(...)` normalmente.

## Como rodar a demo

```bash
mvn exec:java -Dexec.mainClass=com.example.converterframework.demo.ConverterFrameworkDemo
```

Fluxo demonstrado:

1. cria `AddressEntity`, `UserEntity`, `ProductEntity` e `OrderEntity`
2. registra os converters
3. converte `UserEntity -> UserDTO`
4. converte `ProductEntity -> ProductDTO`
5. converte `OrderEntity -> OrderResponse`
6. converte lista de produtos
7. mostra erro de converter inexistente

## Como rodar os testes

```bash
mvn test
```
