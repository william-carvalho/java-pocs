package com.example.corebankledgersingleclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@RestController
public class CoreBankLedgerSingleClassApplication {

    private final Ledger ledger = new Ledger();

    public static void main(String[] args) {
        SpringApplication.run(CoreBankLedgerSingleClassApplication.class, args);
    }

    @PostMapping("/accounts")
    public Account createAccount(@RequestBody AccountRequest request) {
        if (request == null || blank(request.owner)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "owner is required");
        }
        return ledger.createAccount(request.owner);
    }

    @GetMapping("/accounts/{accountId}")
    public Account account(@PathVariable String accountId) {
        return ledger.account(accountId);
    }

    @GetMapping("/accounts/{accountId}/balance")
    public Balance balance(@PathVariable String accountId) {
        return ledger.balance(accountId);
    }

    @PostMapping("/accounts/{accountId}/deposit")
    public LedgerTransaction deposit(@PathVariable String accountId, @RequestBody MoneyRequest request) {
        return ledger.deposit(accountId, amount(request));
    }

    @PostMapping("/accounts/{accountId}/withdraw")
    public LedgerTransaction withdraw(@PathVariable String accountId, @RequestBody MoneyRequest request) {
        return ledger.withdraw(accountId, amount(request));
    }

    @PostMapping("/transfers")
    public LedgerTransaction transfer(@RequestBody TransferRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "transfer request is required");
        }
        return ledger.transfer(request.fromAccountId, request.toAccountId, money(request.amount));
    }

    @GetMapping("/ledger")
    public List<LedgerEntry> entries() {
        return ledger.entries();
    }

    @GetMapping("/transactions")
    public List<LedgerTransaction> transactions() {
        return ledger.transactions();
    }

    private BigDecimal amount(MoneyRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount is required");
        }
        return money(request.amount);
    }

    private BigDecimal money(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be greater than zero");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public Ledger getLedger() {
        return ledger;
    }

    public static class Ledger {
        private final Map<String, Account> accounts = new LinkedHashMap<String, Account>();
        private final List<LedgerTransaction> transactions = new ArrayList<LedgerTransaction>();
        private final List<LedgerEntry> entries = new ArrayList<LedgerEntry>();

        public synchronized Account createAccount(String owner) {
            Account account = new Account(UUID.randomUUID().toString(), owner, true, LocalDateTime.now());
            accounts.put(account.id, account);
            return account;
        }

        public synchronized LedgerTransaction deposit(String accountId, BigDecimal amount) {
            Account account = account(accountId);
            ensureActive(account);

            LedgerTransaction transaction = transaction("DEPOSIT");
            addEntry(transaction.id, account.id, "CREDIT", amount);
            transactions.add(transaction);
            return transaction;
        }

        public synchronized LedgerTransaction withdraw(String accountId, BigDecimal amount) {
            Account account = account(accountId);
            ensureActive(account);
            ensureFunds(account.id, amount);

            LedgerTransaction transaction = transaction("WITHDRAW");
            addEntry(transaction.id, account.id, "DEBIT", amount);
            transactions.add(transaction);
            return transaction;
        }

        public synchronized LedgerTransaction transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
            Account from = account(fromAccountId);
            Account to = account(toAccountId);
            ensureActive(from);
            ensureActive(to);
            ensureFunds(from.id, amount);

            LedgerTransaction transaction = transaction("TRANSFER");
            addEntry(transaction.id, from.id, "DEBIT", amount);
            addEntry(transaction.id, to.id, "CREDIT", amount);
            transactions.add(transaction);
            return transaction;
        }

        public synchronized Account account(String accountId) {
            Account account = accounts.get(accountId);
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "account not found: " + accountId);
            }
            return account;
        }

        public synchronized Balance balance(String accountId) {
            account(accountId);

            BigDecimal value = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            for (LedgerEntry entry : entries) {
                if (entry.accountId.equals(accountId)) {
                    value = "CREDIT".equals(entry.type)
                            ? value.add(entry.amount)
                            : value.subtract(entry.amount);
                }
            }
            return new Balance(accountId, value.setScale(2, RoundingMode.HALF_UP));
        }

        public synchronized List<LedgerEntry> entries() {
            return new ArrayList<LedgerEntry>(entries);
        }

        public synchronized List<LedgerTransaction> transactions() {
            return new ArrayList<LedgerTransaction>(transactions);
        }

        private LedgerTransaction transaction(String type) {
            return new LedgerTransaction(UUID.randomUUID().toString(), type, LocalDateTime.now());
        }

        private void addEntry(String transactionId, String accountId, String type, BigDecimal amount) {
            entries.add(new LedgerEntry(
                    UUID.randomUUID().toString(),
                    transactionId,
                    accountId,
                    type,
                    amount.setScale(2, RoundingMode.HALF_UP),
                    LocalDateTime.now()
            ));
        }

        private void ensureFunds(String accountId, BigDecimal amount) {
            if (balance(accountId).amount.compareTo(amount) < 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "insufficient funds");
            }
        }

        private void ensureActive(Account account) {
            if (!account.active) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "account is inactive");
            }
        }
    }

    public static class AccountRequest {
        public String owner;
    }

    public static class MoneyRequest {
        public BigDecimal amount;
    }

    public static class TransferRequest {
        public String fromAccountId;
        public String toAccountId;
        public BigDecimal amount;
    }

    public static class Account {
        public String id;
        public String owner;
        public boolean active;
        public LocalDateTime createdAt;

        public Account() {
        }

        public Account(String id, String owner, boolean active, LocalDateTime createdAt) {
            this.id = id;
            this.owner = owner;
            this.active = active;
            this.createdAt = createdAt;
        }
    }

    public static class Balance {
        public String accountId;
        public BigDecimal amount;

        public Balance() {
        }

        public Balance(String accountId, BigDecimal amount) {
            this.accountId = accountId;
            this.amount = amount;
        }
    }

    public static class LedgerTransaction {
        public String id;
        public String type;
        public LocalDateTime createdAt;

        public LedgerTransaction() {
        }

        public LedgerTransaction(String id, String type, LocalDateTime createdAt) {
            this.id = id;
            this.type = type;
            this.createdAt = createdAt;
        }
    }

    public static class LedgerEntry {
        public String id;
        public String transactionId;
        public String accountId;
        public String type;
        public BigDecimal amount;
        public LocalDateTime createdAt;

        public LedgerEntry() {
        }

        public LedgerEntry(String id,
                           String transactionId,
                           String accountId,
                           String type,
                           BigDecimal amount,
                           LocalDateTime createdAt) {
            this.id = id;
            this.transactionId = transactionId;
            this.accountId = accountId;
            this.type = type;
            this.amount = amount;
            this.createdAt = createdAt;
        }
    }
}
