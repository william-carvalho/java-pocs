package com.example.ticketsystem.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AvailabilityResponse {

    private Long sessionId;
    private String showName;
    private LocalDateTime eventDateTime;
    private List<AvailabilityZoneResponse> zones;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public List<AvailabilityZoneResponse> getZones() {
        return zones;
    }

    public void setZones(List<AvailabilityZoneResponse> zones) {
        this.zones = zones;
    }
}
