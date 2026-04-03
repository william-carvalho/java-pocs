package com.example.taxsystem.grocery.domain;

import com.example.taxsystem.common.annotation.Mappable;

@Mappable
public class GroceryItem {

    private Long id;
    private String name;
    private GroceryStatus status;
    private GroceryAudit audit;

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

    public GroceryAudit getAudit() {
        return audit;
    }

    public void setAudit(GroceryAudit audit) {
        this.audit = audit;
    }
}
