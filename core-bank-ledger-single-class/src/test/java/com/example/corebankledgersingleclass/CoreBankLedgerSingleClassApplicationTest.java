package com.example.corebankledgersingleclass;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoreBankLedgerSingleClassApplicationTest {

    @Test
    void createsAccountWithZeroBalance() {
        CoreBankLedgerSingleClassApplication.Ledger ledger = new CoreBankLedgerSingleClassApplication.Ledger();

        CoreBankLedgerSingleClassApplication.Account account = ledger.createAccount("Ana");

        assertThat(account.id).isNotBlank();
        assertThat(account.owner).isEqualTo("Ana");
        assertThat(account.active).isTrue();
        assertThat(ledger.balance(account.id).amount).isEqualByComparingTo("0.00");
    }

    @Test
    void depositsMoneyAsCreditEntry() {
        CoreBankLedgerSingleClassApplication.Ledger ledger = new CoreBankLedgerSingleClassApplication.Ledger();
        CoreBankLedgerSingleClassApplication.Account account = ledger.createAccount("Ana");

        CoreBankLedgerSingleClassApplication.LedgerTransaction transaction =
                ledger.deposit(account.id, new BigDecimal("100.00"));

        assertThat(transaction.type).isEqualTo("DEPOSIT");
        assertThat(ledger.balance(account.id).amount).isEqualByComparingTo("100.00");
        assertThat(ledger.entries()).hasSize(1);
        assertThat(ledger.entries().get(0).type).isEqualTo("CREDIT");
    }

    @Test
    void withdrawsMoneyAsDebitEntry() {
        CoreBankLedgerSingleClassApplication.Ledger ledger = new CoreBankLedgerSingleClassApplication.Ledger();
        CoreBankLedgerSingleClassApplication.Account account = ledger.createAccount("Ana");
        ledger.deposit(account.id, new BigDecimal("100.00"));

        CoreBankLedgerSingleClassApplication.LedgerTransaction transaction =
                ledger.withdraw(account.id, new BigDecimal("40.00"));

        assertThat(transaction.type).isEqualTo("WITHDRAW");
        assertThat(ledger.balance(account.id).amount).isEqualByComparingTo("60.00");
        assertThat(ledger.entries()).hasSize(2);
        assertThat(ledger.entries().get(1).type).isEqualTo("DEBIT");
    }

    @Test
    void transfersMoneyWithBalancedDebitAndCreditEntries() {
        CoreBankLedgerSingleClassApplication.Ledger ledger = new CoreBankLedgerSingleClassApplication.Ledger();
        CoreBankLedgerSingleClassApplication.Account from = ledger.createAccount("Ana");
        CoreBankLedgerSingleClassApplication.Account to = ledger.createAccount("Bob");
        ledger.deposit(from.id, new BigDecimal("100.00"));

        CoreBankLedgerSingleClassApplication.LedgerTransaction transfer =
                ledger.transfer(from.id, to.id, new BigDecimal("25.00"));

        assertThat(transfer.type).isEqualTo("TRANSFER");
        assertThat(ledger.balance(from.id).amount).isEqualByComparingTo("75.00");
        assertThat(ledger.balance(to.id).amount).isEqualByComparingTo("25.00");
        assertThat(ledger.entries()).filteredOn("transactionId", transfer.id).hasSize(2);
        assertThat(ledger.entries()).filteredOn("transactionId", transfer.id)
                .extracting("type").containsExactly("DEBIT", "CREDIT");
    }

    @Test
    void rejectsInsufficientFunds() {
        CoreBankLedgerSingleClassApplication.Ledger ledger = new CoreBankLedgerSingleClassApplication.Ledger();
        CoreBankLedgerSingleClassApplication.Account account = ledger.createAccount("Ana");

        assertThatThrownBy(() -> ledger.withdraw(account.id, new BigDecimal("1.00")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("insufficient funds");
    }

    @Test
    void keepsTransactionHistory() {
        CoreBankLedgerSingleClassApplication.Ledger ledger = new CoreBankLedgerSingleClassApplication.Ledger();
        CoreBankLedgerSingleClassApplication.Account account = ledger.createAccount("Ana");

        ledger.deposit(account.id, new BigDecimal("100.00"));
        ledger.withdraw(account.id, new BigDecimal("20.00"));

        assertThat(ledger.transactions()).extracting("type").containsExactly("DEPOSIT", "WITHDRAW");
        assertThat(ledger.entries()).hasSize(2);
    }
}
