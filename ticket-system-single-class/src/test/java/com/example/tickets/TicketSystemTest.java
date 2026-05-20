package com.example.tickets;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketSystemTest {
    @Test
    void sellsMultipleTicketsForDifferentSeatsInTheSameShowSession() {
        TicketSystem system = TicketSystem.withDefaultCatalog();
        TicketSystem.ShowSession session = system.sessions().get(0);

        TicketSystem.Sale sale = system.sellTickets(
                session.getId(),
                "William",
                TicketSystem.seat("VIP", "A10"),
                TicketSystem.seat("VIP", "A11"));

        assertEquals(2, sale.getTotalTickets());
        assertEquals("Rock Night", sale.getSession().getShow().getName());
        assertEquals(LocalDateTime.of(2026, 4, 20, 20, 0), sale.getSession().getEventDateTime());
        assertEquals("VIP", sale.getTickets().get(0).getZone().getName());
        assertEquals("A10", sale.getTickets().get(0).getSeatNumber());
        assertEquals(TicketSystem.TicketStatus.SOLD, sale.getTickets().get(0).getStatus());
    }

    @Test
    void tracksAvailabilityByZoneAfterSale() {
        TicketSystem system = TicketSystem.withDefaultCatalog();
        long sessionId = system.sessions().get(0).getId();

        system.sellTickets(sessionId, "Ana", TicketSystem.seat("VIP", "A1"), TicketSystem.seat("VIP", "A2"));

        TicketSystem.ZoneAvailability vip = system.availability(sessionId).getZones().get(0);

        assertEquals("VIP", vip.getZoneName());
        assertEquals(50, vip.getMaxCapacity());
        assertEquals(2, vip.getSoldCount());
        assertEquals(48, vip.getAvailableCount());
        assertEquals("A1", vip.getOccupiedSeats().get(0));
        assertEquals("A2", vip.getOccupiedSeats().get(1));
    }

    @Test
    void rejectsSeatOutsideTheSelectedZoneRange() {
        TicketSystem system = TicketSystem.withDefaultCatalog();
        long sessionId = system.sessions().get(0).getId();

        assertThrows(IllegalArgumentException.class, () ->
                system.sellTickets(sessionId, "Ana", TicketSystem.seat("VIP", "B1")));
    }

    @Test
    void rejectsDuplicateSeatInSameSessionAndZone() {
        TicketSystem system = TicketSystem.withDefaultCatalog();
        long sessionId = system.sessions().get(0).getId();

        system.sellTickets(sessionId, "Ana", TicketSystem.seat("VIP", "A1"));

        assertThrows(IllegalStateException.class, () ->
                system.sellTickets(sessionId, "Bruno", TicketSystem.seat("VIP", "A1")));
    }

    @Test
    void allowsSameSeatNumberInDifferentSessions() {
        TicketSystem system = TicketSystem.withDefaultCatalog();
        TicketSystem.ShowSession first = system.sessions().get(0);
        TicketSystem.ShowSession second = system.createSession(
                "Rock Night",
                "Arena Floripa",
                LocalDateTime.of(2026, 4, 21, 20, 0));

        system.sellTickets(first.getId(), "Ana", TicketSystem.seat("VIP", "A1"));
        TicketSystem.Sale sale = system.sellTickets(second.getId(), "Bruno", TicketSystem.seat("VIP", "A1"));

        assertEquals(1, sale.getTotalTickets());
        assertEquals(second.getId(), sale.getSession().getId());
    }

    @Test
    void respectsMaximumZoneCapacity() {
        TicketSystem system = new TicketSystem();
        system.addShow("Small Show", "Limited capacity");
        system.addVenue("Small Room", "Sao Paulo");
        system.addZone("Small Room", "FRONT", 2, "F", 1, 2);
        TicketSystem.ShowSession session = system.createSession(
                "Small Show",
                "Small Room",
                LocalDateTime.of(2026, 5, 20, 20, 0));

        system.sellTickets(session.getId(), "Ana", TicketSystem.seat("FRONT", "F1"), TicketSystem.seat("FRONT", "F2"));

        assertThrows(IllegalStateException.class, () ->
                system.sellTickets(session.getId(), "Bruno", TicketSystem.seat("FRONT", "F1")));
    }

    @Test
    void cancellationFreesTheSeatAndAvailability() {
        TicketSystem system = TicketSystem.withDefaultCatalog();
        long sessionId = system.sessions().get(0).getId();

        TicketSystem.Sale sale = system.sellTickets(sessionId, "Ana", TicketSystem.seat("VIP", "A1"));
        system.cancelTicket(sale.getTickets().get(0).getId());
        TicketSystem.Sale secondSale = system.sellTickets(sessionId, "Bruno", TicketSystem.seat("VIP", "A1"));

        assertEquals(1, secondSale.getTotalTickets());
        assertEquals(1, system.availability(sessionId).getZones().get(0).getSoldCount());
    }

    @Test
    void rejectsDuplicateSeatInsideTheSameSaleRequest() {
        TicketSystem system = TicketSystem.withDefaultCatalog();
        long sessionId = system.sessions().get(0).getId();

        assertThrows(IllegalStateException.class, () ->
                system.sellTickets(
                        sessionId,
                        "Ana",
                        TicketSystem.seat("VIP", "A1"),
                        TicketSystem.seat("VIP", "A1")));
    }
}
