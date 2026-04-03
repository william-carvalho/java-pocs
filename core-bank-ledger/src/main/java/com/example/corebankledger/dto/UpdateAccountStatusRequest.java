package com.example.corebankledger.dto;

import com.example.corebankledger.entity.AccountStatus;

import javax.validation.constraints.NotNull;

public class UpdateAccountStatusRequest {

    @NotNull
    private AccountStatus status;

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}

