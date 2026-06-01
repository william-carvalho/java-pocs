# Teachers Class Organizer Single Class

Java 8 POC for organizing and optimizing teacher class sessions.

The production code is intentionally in one class:

```text
src/main/java/com/example/teachersorganizer/TeachersClassOrganizer.java
```

## Rules

- A session needs teacher, class, subject, room, day, start time, and end time.
- Teacher, class, and room conflicts are blocked.
- Cancelled sessions are ignored by conflict checks.
- A room must fit the class student count.
- Suggestions search in 30-minute blocks.
- When possible, suggestions prefer a slot adjacent to another session for the same teacher.

## Example

```java
TeachersClassOrganizer organizer = TeachersClassOrganizer.withDefaultData();

TeachersClassOrganizer.SlotSuggestion suggestion = organizer.suggest(
        "Maria",
        "Class A",
        "Math",
        "Room 101",
        60,
        Arrays.asList(DayOfWeek.MONDAY),
        LocalTime.of(8, 0),
        LocalTime.of(12, 0));
```

## Test

```bash
mvn test
```
