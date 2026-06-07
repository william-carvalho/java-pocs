# Calendar System Single Class

Java 8 POC for booking meetings, removing meetings, listing meetings, and suggesting the best shared time for two people.

The production code is intentionally in one class:

```text
src/main/java/com/example/calendar/CalendarSystem.java
```

## Rules

- A meeting has a title, participants, start time, and end time.
- A person cannot have overlapping active meetings.
- Removing a meeting is a logical cancellation.
- Cancelled meetings do not block future bookings or suggestions.
- `listMeetings` returns active meetings.
- `suggestBestTime` returns the first common free slot for two people, scanning in 30-minute steps.

## Example

```java
CalendarSystem calendar = new CalendarSystem();
calendar.addPerson("Ana", "ana@example.com");
calendar.addPerson("Bob", "bob@example.com");

calendar.bookMeeting(
        "Planning",
        "Weekly planning",
        LocalDateTime.of(2026, 6, 6, 9, 0),
        LocalDateTime.of(2026, 6, 6, 10, 0),
        "ana@example.com",
        "bob@example.com");

CalendarSystem.TimeSuggestion suggestion = calendar.suggestBestTime(
        "ana@example.com",
        "bob@example.com",
        LocalDateTime.of(2026, 6, 6, 9, 0),
        LocalDateTime.of(2026, 6, 6, 18, 0),
        60);
```

## Test

```bash
mvn test
```
