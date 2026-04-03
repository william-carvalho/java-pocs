package com.example.ticketsystem.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class VenueZoneRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "maxCapacity is required")
    @Min(value = 1, message = "maxCapacity must be greater than zero")
    private Integer maxCapacity;

    private String seatPrefix;

    @NotNull(message = "seatStart is required")
    @Min(value = 1, message = "seatStart must be greater than zero")
    private Integer seatStart;

    @NotNull(message = "seatEnd is required")
    @Min(value = 1, message = "seatEnd must be greater than zero")
    private Integer seatEnd;

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
