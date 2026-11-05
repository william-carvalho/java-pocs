# Unique Characters Single Class

Java 8 POC that determines whether every character in a string is unique.
Characters are compared as Unicode code points, and comparison is case-sensitive.

## Implementations

```java
UniqueCharacters.hasAllUniqueCharacters("abcdef");
```

Uses a `HashSet`, taking O(n) time and O(n) additional space.

```java
UniqueCharacters.hasAllUniqueCharactersWithoutDataStructure("abcdef");
```

Uses nested comparisons, taking O(n²) time and O(1) additional space.

Both methods return `true` for an empty string and throw
`IllegalArgumentException` for `null`.

## Test

```bash
mvn test
```
