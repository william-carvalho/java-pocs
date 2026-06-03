# Converter Framework Single Class

Java 8 POC for converting complex types with a registry-based converter framework.

The production code is intentionally in one class:

```text
src/main/java/com/example/converter/ConverterFramework.java
```

## Features

- Register converters by source and target type.
- Convert single objects.
- Convert lists.
- Convert nested objects.
- Map fields with different names.
- Build derived fields.
- Return a clear error when no converter exists.

## Example

```java
ConverterFramework.ConversionService service = ConverterFramework.defaultService();

ConverterFramework.UserDTO dto = service.convert(userEntity, ConverterFramework.UserDTO.class);
List<ConverterFramework.ProductDTO> products =
        service.convertList(productEntities, ConverterFramework.ProductDTO.class);
```

## Test

```bash
mvn test
```
