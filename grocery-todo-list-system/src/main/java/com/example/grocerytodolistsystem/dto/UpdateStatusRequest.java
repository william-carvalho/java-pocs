package com.example.grocerytodolistsystem.dto;

import com.example.grocerytodolistsystem.enums.ItemStatus;

import javax.validation.constraints.NotNull;

public class UpdateStatusRequest {

    @NotNull(message = "status is required")
    private ItemStatus status;

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }
}
