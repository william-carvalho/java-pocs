package com.example.corebankledger.dto;

import com.example.corebankledger.entity.Account;
import com.example.corebankledger.entity.AccountStatus;
import com.example.corebankledger.entity.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountResponse {

    private Long id;
    private String accountNumber;
    private String ownerName;
    private AccountType type;
    private AccountStatus status;
    private BigDecimal currentBalance;
    private LocalDateTime createdAt;

    public static AccountResponse from(Account account) {
        AccountResponse response = new AccountResponse();
        response.id = account.getId();
        response.accountNumber = account.getAccountNumber();
        response.ownerName = account.getOwnerName();
        response.type = account.getType();
        response.status = account.getStatus();
        response.currentBalance = account.getCurrentBalance();
        response.createdAt = account.getCreatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public AccountType getType() {
        return type;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

