# DontPad Single Class

Java 8 POC for a DontPad-style shared text pad.

The production code is intentionally in one class:

```text
src/main/java/com/example/dontpad/DontPad.java
```

## Rules

- Each pad is addressed by a unique slug.
- Slugs must match `[a-zA-Z0-9_-]+`.
- `getOrCreate` creates an empty pad when the slug does not exist.
- `updatePad` creates or updates content by slug.
- Content can be empty.
- `deletePad` removes an existing pad.
- `listPads` returns summaries in creation order.
- Each update increments the pad version.

## Example

```java
DontPad dontPad = new DontPad();

DontPad.Pad pad = dontPad.getOrCreate("team-notes");
dontPad.updatePad("team-notes", "Shared text");

List<DontPad.PadSummary> pads = dontPad.listPads();
```

## Test

```bash
mvn test
```
