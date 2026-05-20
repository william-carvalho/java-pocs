package com.example.tickets;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class TicketSystem {
    private final Map<String, Show> showsByName = new LinkedHashMap<String, Show>();
    private final Map<String, Venue> venuesByName = new LinkedHashMap<String, Venue>();
    private final List<ShowSession> sessions = new ArrayList<ShowSession>();
    private final List<Ticket> tickets = new ArrayList<Ticket>();
    private long nextSessionId = 1;
    private long nextTicketId = 1;

    public Show addShow(String name, String description) {
        Show show = new Show(name, description);
        showsByName.put(show.getName(), show);
        return show;
    }

    public Venue addVenue(String name, String city) {
        Venue venue = new Venue(name, city);
        venuesByName.put(venue.getName(), venue);
        return venue;
    }

    public Zone addZone(String venueName, String zoneName, int maxCapacity, String seatPrefix, int seatStart, int seatEnd) {
        Venue venue = findVenue(venueName);
        Zone zone = new Zone(zoneName, maxCapacity, seatPrefix, seatStart, seatEnd);
        venue.addZone(zone);
        return zone;
    }

    public ShowSession createSession(String showName, String venueName, LocalDateTime eventDateTime) {
        Show show = findShow(showName);
        Venue venue = findVenue(venueName);
        ShowSession session = new ShowSession(nextSessionId++, show, venue, eventDateTime);
        sessions.add(session);
        return session;
    }

    public Sale sellTickets(long sessionId, String customerName, TicketRequest... requests) {
        if (isBlank(customerName)) {
            throw new IllegalArgumentException("customerName is required");
        }
        if (requests == null || requests.length == 0) {
            throw new IllegalArgumentException("at least one ticket is required");
        }

        ShowSession session = findSession(sessionId);
        Set<String> requestedSeats = new LinkedHashSet<String>();
        for (TicketRequest request : requests) {
            validateRequest(session, request, requestedSeats);
        }

        List<Ticket> sold = new ArrayList<Ticket>();
        for (TicketRequest request : requests) {
            Zone zone = session.getVenue().findZone(request.zoneName);
            Ticket ticket = new Ticket(nextTicketId++, session, customerName.trim(), zone, request.seatNumber);
            tickets.add(ticket);
            sold.add(ticket);
        }

        return new Sale(session, customerName.trim(), sold);
    }

    public void cancelTicket(long ticketId) {
        findTicket(ticketId).cancel();
    }

    public Availability availability(long sessionId) {
        ShowSession session = findSession(sessionId);
        List<ZoneAvailability> zones = new ArrayList<ZoneAvailability>();
        for (Zone zone : session.getVenue().getZones()) {
            List<String> occupiedSeats = occupiedSeats(session, zone);
            zones.add(new ZoneAvailability(
                    zone.getName(),
                    zone.getMaxCapacity(),
                    occupiedSeats.size(),
                    zone.getMaxCapacity() - occupiedSeats.size(),
                    occupiedSeats));
        }
        return new Availability(session, zones);
    }

    public List<ShowSession> sessions() {
        return Collections.unmodifiableList(sessions);
    }

    public List<Ticket> tickets() {
        return Collections.unmodifiableList(tickets);
    }

    public List<Show> shows() {
        return Collections.unmodifiableList(new ArrayList<Show>(showsByName.values()));
    }

    public List<Venue> venues() {
        return Collections.unmodifiableList(new ArrayList<Venue>(venuesByName.values()));
    }

    public static TicketRequest seat(String zoneName, String seatNumber) {
        return new TicketRequest(zoneName, seatNumber);
    }

    public static TicketSystem withDefaultCatalog() {
        TicketSystem system = new TicketSystem();
        system.addShow("Rock Night", "Live rock concert");
        system.addShow("Jazz Festival", "Evening jazz performance");
        system.addVenue("Arena Floripa", "Florianopolis");
        system.addVenue("Teatro Central", "Sao Paulo");
        system.addZone("Arena Floripa", "VIP", 50, "A", 1, 50);
        system.addZone("Arena Floripa", "PREMIUM", 100, "B", 1, 100);
        system.addZone("Arena Floripa", "STANDARD", 300, "C", 1, 300);
        system.addZone("Teatro Central", "PLATEA", 80, "D", 1, 80);
        system.addZone("Teatro Central", "BALCONY", 120, "E", 1, 120);
        system.createSession("Rock Night", "Arena Floripa", LocalDateTime.of(2026, 4, 20, 20, 0));
        system.createSession("Jazz Festival", "Teatro Central", LocalDateTime.of(2026, 4, 25, 21, 0));
        return system;
    }

    private void validateRequest(ShowSession session, TicketRequest request, Set<String> requestedSeats) {
        Objects.requireNonNull(request, "ticket request");
        Zone zone = session.getVenue().findZone(request.zoneName);
        zone.validateSeat(request.seatNumber);

        String key = zone.getName() + "|" + request.seatNumber;
        if (!requestedSeats.add(key)) {
            throw new IllegalStateException("Seat requested more than once: " + request.seatNumber);
        }
        if (isSeatSold(session, zone, request.seatNumber)) {
            throw new IllegalStateException("Seat already sold: " + request.seatNumber);
        }
        if (soldCount(session, zone) >= zone.getMaxCapacity()) {
            throw new IllegalStateException("Zone capacity reached: " + zone.getName());
        }
        if (soldCount(session) >= session.getVenue().maxCapacity()) {
            throw new IllegalStateException("Venue capacity reached: " + session.getVenue().getName());
        }
    }

    private boolean isSeatSold(ShowSession session, Zone zone, String seatNumber) {
        for (Ticket ticket : tickets) {
            if (ticket.matches(session, zone, seatNumber) && ticket.getStatus() == TicketStatus.SOLD) {
                return true;
            }
        }
        return false;
    }

    private int soldCount(ShowSession session) {
        int count = 0;
        for (Ticket ticket : tickets) {
            if (ticket.getSession().equals(session) && ticket.getStatus() == TicketStatus.SOLD) {
                count++;
            }
        }
        return count;
    }

    private int soldCount(ShowSession session, Zone zone) {
        int count = 0;
        for (Ticket ticket : tickets) {
            if (ticket.getSession().equals(session) && ticket.getZone().equals(zone) && ticket.getStatus() == TicketStatus.SOLD) {
                count++;
            }
        }
        return count;
    }

    private List<String> occupiedSeats(ShowSession session, Zone zone) {
        List<String> occupiedSeats = new ArrayList<String>();
        for (Ticket ticket : tickets) {
            if (ticket.getSession().equals(session) && ticket.getZone().equals(zone) && ticket.getStatus() == TicketStatus.SOLD) {
                occupiedSeats.add(ticket.getSeatNumber());
            }
        }
        return Collections.unmodifiableList(occupiedSeats);
    }

    private Show findShow(String name) {
        Show show = showsByName.get(trim(name));
        if (show == null) {
            throw new IllegalArgumentException("Unknown show: " + name);
        }
        return show;
    }

    private Venue findVenue(String name) {
        Venue venue = venuesByName.get(trim(name));
        if (venue == null) {
            throw new IllegalArgumentException("Unknown venue: " + name);
        }
        return venue;
    }

    private ShowSession findSession(long id) {
        for (ShowSession session : sessions) {
            if (session.getId() == id) {
                return session;
            }
        }
        throw new IllegalArgumentException("Unknown session: " + id);
    }

    private Ticket findTicket(long id) {
        for (Ticket ticket : tickets) {
            if (ticket.getId() == id) {
                return ticket;
            }
        }
        throw new IllegalArgumentException("Unknown ticket: " + id);
    }

    private static String trim(String text) {
        return text == null ? null : text.trim();
    }

    private static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    public enum TicketStatus {
        SOLD,
        CANCELLED
    }

    public static final class Show {
        private final String name;
        private final String description;

        private Show(String name, String description) {
            if (isBlank(name)) {
                throw new IllegalArgumentException("show name is required");
            }
            this.name = name.trim();
            this.description = description == null ? "" : description.trim();
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    public static final class Venue {
        private final String name;
        private final String city;
        private final Map<String, Zone> zonesByName = new LinkedHashMap<String, Zone>();

        private Venue(String name, String city) {
            if (isBlank(name)) {
                throw new IllegalArgumentException("venue name is required");
            }
            if (isBlank(city)) {
                throw new IllegalArgumentException("city is required");
            }
            this.name = name.trim();
            this.city = city.trim();
        }

        private void addZone(Zone zone) {
            zonesByName.put(zone.getName(), zone);
        }

        private Zone findZone(String zoneName) {
            Zone zone = zonesByName.get(trim(zoneName));
            if (zone == null) {
                throw new IllegalArgumentException("Zone does not belong to venue: " + zoneName);
            }
            return zone;
        }

        private int maxCapacity() {
            int capacity = 0;
            for (Zone zone : zonesByName.values()) {
                capacity += zone.getMaxCapacity();
            }
            return capacity;
        }

        public String getName() {
            return name;
        }

        public String getCity() {
            return city;
        }

        public List<Zone> getZones() {
            return Collections.unmodifiableList(new ArrayList<Zone>(zonesByName.values()));
        }
    }

    public static final class Zone {
        private final String name;
        private final int maxCapacity;
        private final String seatPrefix;
        private final int seatStart;
        private final int seatEnd;

        private Zone(String name, int maxCapacity, String seatPrefix, int seatStart, int seatEnd) {
            if (isBlank(name)) {
                throw new IllegalArgumentException("zone name is required");
            }
            if (maxCapacity <= 0) {
                throw new IllegalArgumentException("maxCapacity must be greater than zero");
            }
            if (isBlank(seatPrefix)) {
                throw new IllegalArgumentException("seatPrefix is required");
            }
            if (seatStart <= 0 || seatEnd < seatStart) {
                throw new IllegalArgumentException("invalid seat range");
            }
            if ((seatEnd - seatStart + 1) < maxCapacity) {
                throw new IllegalArgumentException("seat range must cover maxCapacity");
            }
            this.name = name.trim();
            this.maxCapacity = maxCapacity;
            this.seatPrefix = seatPrefix.trim();
            this.seatStart = seatStart;
            this.seatEnd = seatEnd;
        }

        private void validateSeat(String seatNumber) {
            if (isBlank(seatNumber) || !seatNumber.startsWith(seatPrefix)) {
                throw new IllegalArgumentException("Seat is outside zone range: " + seatNumber);
            }
            String numericPart = seatNumber.substring(seatPrefix.length());
            try {
                int value = Integer.parseInt(numericPart);
                if (value < seatStart || value > seatEnd) {
                    throw new IllegalArgumentException("Seat is outside zone range: " + seatNumber);
                }
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Seat is outside zone range: " + seatNumber);
            }
        }

        public String getName() {
            return name;
        }

        public int getMaxCapacity() {
            return maxCapacity;
        }

        public String getSeatPrefix() {
            return seatPrefix;
        }

        public int getSeatStart() {
            return seatStart;
        }

        public int getSeatEnd() {
            return seatEnd;
        }
    }

    public static final class ShowSession {
        private final long id;
        private final Show show;
        private final Venue venue;
        private final LocalDateTime eventDateTime;

        private ShowSession(long id, Show show, Venue venue, LocalDateTime eventDateTime) {
            this.id = id;
            this.show = show;
            this.venue = venue;
            this.eventDateTime = Objects.requireNonNull(eventDateTime, "eventDateTime");
        }

        public long getId() {
            return id;
        }

        public Show getShow() {
            return show;
        }

        public Venue getVenue() {
            return venue;
        }

        public LocalDateTime getEventDateTime() {
            return eventDateTime;
        }
    }

    public static final class TicketRequest {
        private final String zoneName;
        private final String seatNumber;

        private TicketRequest(String zoneName, String seatNumber) {
            if (isBlank(zoneName)) {
                throw new IllegalArgumentException("zoneName is required");
            }
            if (isBlank(seatNumber)) {
                throw new IllegalArgumentException("seatNumber is required");
            }
            this.zoneName = zoneName.trim();
            this.seatNumber = seatNumber.trim();
        }
    }

    public static final class Ticket {
        private final long id;
        private final ShowSession session;
        private final String customerName;
        private final Zone zone;
        private final String seatNumber;
        private TicketStatus status = TicketStatus.SOLD;

        private Ticket(long id, ShowSession session, String customerName, Zone zone, String seatNumber) {
            this.id = id;
            this.session = session;
            this.customerName = customerName;
            this.zone = zone;
            this.seatNumber = seatNumber;
        }

        private boolean matches(ShowSession session, Zone zone, String seatNumber) {
            return this.session.equals(session) && this.zone.equals(zone) && this.seatNumber.equals(seatNumber);
        }

        private void cancel() {
            status = TicketStatus.CANCELLED;
        }

        public long getId() {
            return id;
        }

        public ShowSession getSession() {
            return session;
        }

        public String getCustomerName() {
            return customerName;
        }

        public Zone getZone() {
            return zone;
        }

        public String getSeatNumber() {
            return seatNumber;
        }

        public TicketStatus getStatus() {
            return status;
        }
    }

    public static final class Sale {
        private final ShowSession session;
        private final String customerName;
        private final List<Ticket> tickets;

        private Sale(ShowSession session, String customerName, List<Ticket> tickets) {
            this.session = session;
            this.customerName = customerName;
            this.tickets = Collections.unmodifiableList(new ArrayList<Ticket>(tickets));
        }

        public ShowSession getSession() {
            return session;
        }

        public String getCustomerName() {
            return customerName;
        }

        public List<Ticket> getTickets() {
            return tickets;
        }

        public int getTotalTickets() {
            return tickets.size();
        }
    }

    public static final class Availability {
        private final ShowSession session;
        private final List<ZoneAvailability> zones;

        private Availability(ShowSession session, List<ZoneAvailability> zones) {
            this.session = session;
            this.zones = Collections.unmodifiableList(new ArrayList<ZoneAvailability>(zones));
        }

        public ShowSession getSession() {
            return session;
        }

        public List<ZoneAvailability> getZones() {
            return zones;
        }
    }

    public static final class ZoneAvailability {
        private final String zoneName;
        private final int maxCapacity;
        private final int soldCount;
        private final int availableCount;
        private final List<String> occupiedSeats;

        private ZoneAvailability(String zoneName, int maxCapacity, int soldCount, int availableCount, List<String> occupiedSeats) {
            this.zoneName = zoneName;
            this.maxCapacity = maxCapacity;
            this.soldCount = soldCount;
            this.availableCount = availableCount;
            this.occupiedSeats = Collections.unmodifiableList(new ArrayList<String>(occupiedSeats));
        }

        public String getZoneName() {
            return zoneName;
        }

        public int getMaxCapacity() {
            return maxCapacity;
        }

        public int getSoldCount() {
            return soldCount;
        }

        public int getAvailableCount() {
            return availableCount;
        }

        public List<String> getOccupiedSeats() {
            return occupiedSeats;
        }
    }
}
