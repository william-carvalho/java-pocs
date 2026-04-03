package com.example.ticketsystem.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ShowSessionRequest {

    @NotNull(message = "showId is required")
    private Long showId;

    @NotNull(message = "venueId is required")
    private Long venueId;

    @NotNull(message = "eventDateTime is required")
    private LocalDateTime eventDateTime;

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }
}
