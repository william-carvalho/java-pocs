# Ticket System Single Class

Java 8 POC for selling tickets by show, venue, zone, date, and selected seat.

The production code is intentionally in one class:

```text
src/main/java/com/example/tickets/TicketSystem.java
```

## Rules

- A show can have sessions on different dates.
- A session belongs to one show and one venue.
- A venue has zones, each with a max capacity and valid seat range.
- A sale can include multiple tickets.
- Seats must belong to the chosen zone.
- The same seat cannot be sold twice for the same session and zone.
- Capacity is respected per zone and per venue.
- Cancelling a ticket frees the seat again.

## Example

```java
TicketSystem system = TicketSystem.withDefaultCatalog();
TicketSystem.ShowSession session = system.sessions().get(0);

TicketSystem.Sale sale = system.sellTickets(
        session.getId(),
        "William",
        TicketSystem.seat("VIP", "A10"),
        TicketSystem.seat("VIP", "A11"));
```

## Test

```bash
mvn test
```
