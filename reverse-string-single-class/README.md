# Reverse String Single Class

Java 8 POC for reversing strings.

The production code is intentionally in one class:

```text
src/main/java/com/example/reversestring/ReverseString.java
```

## Features

- Reverse a full string.
- Reverse word order.
- Reverse each word while preserving word order.
- Check if a string is a palindrome.
- Reject `null` input with a clear exception.

## Example

```java
String reversed = ReverseString.reverse("hello");
String words = ReverseString.reverseWords("one two three");
boolean palindrome = ReverseString.isPalindrome("level");
```

## Test

```bash
mvn test
```
