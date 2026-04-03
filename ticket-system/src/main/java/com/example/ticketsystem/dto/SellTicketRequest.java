package com.example.ticketsystem.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class SellTicketRequest {

    @NotNull(message = "sessionId is required")
    private Long sessionId;

    private String customerName;

    @Valid
    @NotEmpty(message = "items must not be empty")
    private List<SellTicketItemRequest> items;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<SellTicketItemRequest> getItems() {
        return items;
    }

    public void setItems(List<SellTicketItemRequest> items) {
        this.items = items;
    }
}
