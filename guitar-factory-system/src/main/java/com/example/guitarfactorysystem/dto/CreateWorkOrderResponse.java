package com.example.guitarfactorysystem.dto;

import com.example.guitarfactorysystem.enums.WorkOrderStatus;

import java.time.LocalDateTime;

public class CreateWorkOrderResponse {

    private Long id;
    private Long customGuitarOrderId;
    private String workOrderNumber;
    private WorkOrderStatus status;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomGuitarOrderId() {
        return customGuitarOrderId;
    }

    public void setCustomGuitarOrderId(Long customGuitarOrderId) {
        this.customGuitarOrderId = customGuitarOrderId;
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
}
