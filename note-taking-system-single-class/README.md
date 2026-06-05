# Note Taking System Single Class

Java 8 POC for adding, saving, editing, deleting, and syncing notes.

The production code is intentionally in one class:

```text
src/main/java/com/example/notes/NoteTakingSystem.java
```

## Rules

- `addNote` creates a draft note with version `1`.
- `saveNote` marks a note as saved.
- `editNote` updates title/content, increments version, and marks the note as draft again.
- `deleteNote` performs a logical delete, increments version, and keeps the note available for sync.
- `listNotes` returns only non-deleted notes.
- `listAllNotes` includes deleted notes.
- `sync(updatedAfter)` returns notes updated after the timestamp, including deleted notes.

## Example

```java
NoteTakingSystem notes = new NoteTakingSystem();

NoteTakingSystem.Note note = notes.addNote("Shopping", "Milk");
notes.saveNote(note.getId());
notes.editNote(note.getId(), "Shopping", "Milk and bread");

NoteTakingSystem.SyncResult changes = notes.sync(lastSyncTime);
```

## Test

```bash
mvn test
```
