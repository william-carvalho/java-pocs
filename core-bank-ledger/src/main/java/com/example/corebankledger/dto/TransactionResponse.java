package com.example.corebankledger.dto;

import com.example.corebankledger.entity.LedgerTransaction;
import com.example.corebankledger.entity.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionResponse {

    private Long transactionId;
    private String transactionReference;
    private TransactionType type;
    private String description;
    private LocalDateTime createdAt;
    private List<LedgerEntryResponse> entries;

    public static TransactionResponse of(LedgerTransaction transaction, List<LedgerEntryResponse> entries) {
        TransactionResponse response = new TransactionResponse();
        response.transactionId = transaction.getId();
        response.transactionReference = transaction.getTransactionReference();
        response.type = transaction.getType();
        response.description = transaction.getDescription();
        response.createdAt = transaction.getCreatedAt();
        response.entries = entries;
        return response;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public TransactionType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<LedgerEntryResponse> getEntries() {
        return entries;
    }
}

