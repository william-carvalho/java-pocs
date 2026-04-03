package com.example.corebankledger.dto;

import java.math.BigDecimal;

public class BalanceResponse {

    private final Long accountId;
    private final String accountNumber;
    private final BigDecimal currentBalance;

    public BalanceResponse(Long accountId, String accountNumber, BigDecimal currentBalance) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
}

