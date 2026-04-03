package com.example.guitarfactorysystem.dto;

import com.example.guitarfactorysystem.enums.WorkOrderStatus;

import javax.validation.constraints.NotNull;

public class UpdateWorkOrderStatusRequest {

    @NotNull(message = "status is required")
    private WorkOrderStatus status;

    public WorkOrderStatus getStatus() {
        return status;
    }

    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }
}
