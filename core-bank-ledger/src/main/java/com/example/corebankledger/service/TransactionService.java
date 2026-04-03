package com.example.corebankledger.service;

import com.example.corebankledger.dto.MoneyOperationRequest;
import com.example.corebankledger.dto.TransactionResponse;
import com.example.corebankledger.dto.TransferRequest;
import com.example.corebankledger.entity.Account;
import com.example.corebankledger.entity.EntryType;
import com.example.corebankledger.entity.LedgerEntry;
import com.example.corebankledger.entity.LedgerTransaction;
import com.example.corebankledger.entity.TransactionType;
import com.example.corebankledger.exception.BusinessException;
import com.example.corebankledger.repository.AccountRepository;
import com.example.corebankledger.repository.LedgerEntryRepository;
import com.example.corebankledger.repository.LedgerTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final LedgerTransactionRepository ledgerTransactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final LedgerService ledgerService;

    public TransactionService(AccountService accountService,
                              AccountRepository accountRepository,
                              LedgerTransactionRepository ledgerTransactionRepository,
                              LedgerEntryRepository ledgerEntryRepository,
                              LedgerService ledgerService) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.ledgerTransactionRepository = ledgerTransactionRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.ledgerService = ledgerService;
    }

    @Transactional
    public TransactionResponse credit(MoneyOperationRequest request) {
        Account account = accountService.findEntity(request.getAccountId());
        accountService.ensureActive(account);
        BigDecimal amount = normalizeAmount(request.getAmount());

        LedgerTransaction transaction = createTransaction(TransactionType.CREDIT, request.getDescription());
        BigDecimal newBalance = MoneyUtils.scale(account.getCurrentBalance().add(amount));
        account.setCurrentBalance(newBalance);
        accountRepository.save(account);

        saveEntry(transaction, account, EntryType.CREDIT, amount, newBalance);
        return ledgerService.toTransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse debit(MoneyOperationRequest request) {
        Account account = accountService.findEntity(request.getAccountId());
        accountService.ensureActive(account);
        BigDecimal amount = normalizeAmount(request.getAmount());
        ensureSufficientBalance(account, amount);

        LedgerTransaction transaction = createTransaction(TransactionType.DEBIT, request.getDescription());
        BigDecimal newBalance = MoneyUtils.scale(account.getCurrentBalance().subtract(amount));
        account.setCurrentBalance(newBalance);
        accountRepository.save(account);

        saveEntry(transaction, account, EntryType.DEBIT, amount, newBalance);
        return ledgerService.toTransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new BusinessException("Source and destination accounts must be different");
        }

        Account fromAccount = accountService.findEntity(request.getFromAccountId());
        Account toAccount = accountService.findEntity(request.getToAccountId());
        accountService.ensureActive(fromAccount);
        accountService.ensureActive(toAccount);

        BigDecimal amount = normalizeAmount(request.getAmount());
        ensureSufficientBalance(fromAccount, amount);

        LedgerTransaction transaction = createTransaction(TransactionType.TRANSFER, request.getDescription());

        BigDecimal sourceBalance = MoneyUtils.scale(fromAccount.getCurrentBalance().subtract(amount));
        BigDecimal destinationBalance = MoneyUtils.scale(toAccount.getCurrentBalance().add(amount));

        fromAccount.setCurrentBalance(sourceBalance);
        toAccount.setCurrentBalance(destinationBalance);
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        saveEntry(transaction, fromAccount, EntryType.DEBIT, amount, sourceBalance);
        saveEntry(transaction, toAccount, EntryType.CREDIT, amount, destinationBalance);

        return ledgerService.toTransactionResponse(transaction);
    }

    private LedgerTransaction createTransaction(TransactionType type, String description) {
        LedgerTransaction transaction = new LedgerTransaction();
        transaction.setType(type);
        transaction.setDescription(description);
        transaction.setTransactionReference("TMP-" + java.util.UUID.randomUUID());
        LedgerTransaction saved = ledgerTransactionRepository.save(transaction);
        saved.setTransactionReference(String.format("TXN-%s-%06d",
                java.time.LocalDate.now().toString().replace("-", ""),
                saved.getId()));
        return ledgerTransactionRepository.save(saved);
    }

    private void saveEntry(LedgerTransaction transaction,
                           Account account,
                           EntryType entryType,
                           BigDecimal amount,
                           BigDecimal balanceAfter) {
        LedgerEntry entry = new LedgerEntry();
        entry.setTransaction(transaction);
        entry.setAccount(account);
        entry.setEntryType(entryType);
        entry.setAmount(amount);
        entry.setBalanceAfter(balanceAfter);
        ledgerEntryRepository.save(entry);
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be greater than zero");
        }
        return MoneyUtils.scale(amount);
    }

    private void ensureSufficientBalance(Account account, BigDecimal amount) {
        if (account.getCurrentBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient balance for account: " + account.getId());
        }
    }
}
