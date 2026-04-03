package com.example.taxsystem.grocery.api;

import com.example.taxsystem.common.annotation.Mappable;
import com.example.taxsystem.grocery.domain.GroceryStatus;

@Mappable
public class GroceryItemResponse {

    private Long id;
    private String name;
    private GroceryStatus status;
    private GroceryAuditResponse audit;

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

    public GroceryStatus getStatus() {
        return status;
    }

    public void setStatus(GroceryStatus status) {
        this.status = status;
    }

    public GroceryAuditResponse getAudit() {
        return audit;
    }

    public void setAudit(GroceryAuditResponse audit) {
        this.audit = audit;
    }
}
