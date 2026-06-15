# Custom String Single Class

Java 8 POC for a custom immutable string implementation backed by `char[]`.

The production code is intentionally in one class:

```text
src/main/java/com/example/customstring/CustomString.java
```

## Implemented Methods

- `toArray`
- `foreach`
- `reverse`
- `iterator`
- `length`
- `charAt`
- `equals`
- `isEmpty`
- `replace`
- `substring`
- `trim`
- `toJson`
- `indexOf`
- `hashCode`
- `toString`

## Example

```java
CustomString text = new CustomString(" hello ");

text.length();
text.charAt(1);
text.trim();
text.reverse();
text.replace('l', 'x');
text.toJson();
```

## Test

```bash
mvn test
```
