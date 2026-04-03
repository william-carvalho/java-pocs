package com.example.corebankledger.dto;

import com.example.corebankledger.entity.EntryType;
import com.example.corebankledger.entity.LedgerEntry;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LedgerEntryResponse {

    private Long id;
    private Long accountId;
    private EntryType entryType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private LocalDateTime createdAt;

    public static LedgerEntryResponse from(LedgerEntry entry) {
        LedgerEntryResponse response = new LedgerEntryResponse();
        response.id = entry.getId();
        response.accountId = entry.getAccount().getId();
        response.entryType = entry.getEntryType();
        response.amount = entry.getAmount();
        response.balanceAfter = entry.getBalanceAfter();
        response.createdAt = entry.getCreatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

