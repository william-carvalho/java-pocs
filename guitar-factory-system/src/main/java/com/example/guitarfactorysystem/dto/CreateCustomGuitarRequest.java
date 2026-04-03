package com.example.guitarfactorysystem.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CreateCustomGuitarRequest {

    @NotBlank(message = "customerName is required")
    private String customerName;

    @NotNull(message = "guitarModelId is required")
    private Long guitarModelId;

    @Valid
    @NotEmpty(message = "items must not be empty")
    private List<CreateCustomGuitarItemRequest> items;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getGuitarModelId() {
        return guitarModelId;
    }

    public void setGuitarModelId(Long guitarModelId) {
        this.guitarModelId = guitarModelId;
    }

    public List<CreateCustomGuitarItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CreateCustomGuitarItemRequest> items) {
        this.items = items;
    }
}
