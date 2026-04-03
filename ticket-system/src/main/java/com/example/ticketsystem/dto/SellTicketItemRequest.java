package com.example.ticketsystem.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SellTicketItemRequest {

    @NotNull(message = "zoneId is required")
    private Long zoneId;

    @NotBlank(message = "seatNumber is required")
    private String seatNumber;

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}
