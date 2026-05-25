# File Share Single Class

Java 8 POC for saving, restoring, deleting, listing, and searching encrypted files.

The production code is intentionally in one class:

```text
src/main/java/com/example/fileshare/FileShareSystem.java
```

## Rules

- `saveFile` stores file metadata and encrypted bytes.
- `restoreFile` decrypts and returns the original bytes.
- `deleteFile` performs a logical delete.
- `listFiles` returns only active files.
- `search` matches active files by file name or content type.
- Deleted files cannot be restored.

## Example

```java
FileShareSystem files = new FileShareSystem("1234567890123456");

FileShareSystem.StoredFile saved = files.saveFile(
        "contract.txt",
        "text/plain",
        "signed contract".getBytes(StandardCharsets.UTF_8));

byte[] restored = files.restoreFile(saved.getId());
```

## Test

```bash
mvn test
```
