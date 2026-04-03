package com.example.corebankledger.dto;

import com.example.corebankledger.entity.AccountType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CreateAccountRequest {

    @NotBlank
    private String ownerName;

    @NotNull
    private AccountType type;

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }
}

