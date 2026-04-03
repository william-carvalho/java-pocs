package com.example.ticketsystem.dto;

public class VenueZoneResponse {

    private Long id;
    private String name;
    private Integer maxCapacity;
    private String seatPrefix;
    private Integer seatStart;
    private Integer seatEnd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getSeatPrefix() {
        return seatPrefix;
    }

    public void setSeatPrefix(String seatPrefix) {
        this.seatPrefix = seatPrefix;
    }

    public Integer getSeatStart() {
        return seatStart;
    }

    public void setSeatStart(Integer seatStart) {
        this.seatStart = seatStart;
    }

    public Integer getSeatEnd() {
        return seatEnd;
    }

    public void setSeatEnd(Integer seatEnd) {
        this.seatEnd = seatEnd;
    }
}
