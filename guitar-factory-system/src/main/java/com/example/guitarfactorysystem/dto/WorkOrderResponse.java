package com.example.guitarfactorysystem.dto;

import com.example.guitarfactorysystem.enums.WorkOrderStatus;

import java.time.LocalDateTime;

public class WorkOrderResponse {

    private Long id;
    private String workOrderNumber;
    private WorkOrderStatus status;
    private LocalDateTime createdAt;
    private Long customGuitarOrderId;
    private String customerName;
    private String guitarModelName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkOrderNumber() {
        return workOrderNumber;
    }

    public void setWorkOrderNumber(String workOrderNumber) {
        this.workOrderNumber = workOrderNumber;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCustomGuitarOrderId() {
        return customGuitarOrderId;
    }

    public void setCustomGuitarOrderId(Long customGuitarOrderId) {
        this.customGuitarOrderId = customGuitarOrderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getGuitarModelName() {
        return guitarModelName;
    }

    public void setGuitarModelName(String guitarModelName) {
        this.guitarModelName = guitarModelName;
    }
}
