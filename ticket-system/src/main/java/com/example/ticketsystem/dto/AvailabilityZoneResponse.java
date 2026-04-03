package com.example.ticketsystem.dto;

import java.util.List;

public class AvailabilityZoneResponse {

    private Long zoneId;
    private String zoneName;
    private Integer maxCapacity;
    private Integer soldCount;
    private Integer availableCount;
    private List<String> occupiedSeats;
    private List<String> availableSeats;

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(Integer soldCount) {
        this.soldCount = soldCount;
    }

    public Integer getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(Integer availableCount) {
        this.availableCount = availableCount;
    }

    public List<String> getOccupiedSeats() {
        return occupiedSeats;
    }

    public void setOccupiedSeats(List<String> occupiedSeats) {
        this.occupiedSeats = occupiedSeats;
    }

    public List<String> getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(List<String> availableSeats) {
        this.availableSeats = availableSeats;
    }
}
