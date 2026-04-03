package com.example.corebankledger.service;

import com.example.corebankledger.dto.LedgerEntryResponse;
import com.example.corebankledger.dto.TransactionResponse;
import com.example.corebankledger.entity.LedgerEntry;
import com.example.corebankledger.entity.LedgerTransaction;
import com.example.corebankledger.entity.TransactionType;
import com.example.corebankledger.exception.NotFoundException;
import com.example.corebankledger.repository.LedgerEntryRepository;
import com.example.corebankledger.repository.LedgerTransactionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LedgerService {

    private final LedgerTransactionRepository ledgerTransactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final AccountService accountService;

    public LedgerService(LedgerTransactionRepository ledgerTransactionRepository,
                         LedgerEntryRepository ledgerEntryRepository,
                         AccountService accountService) {
        this.ledgerTransactionRepository = ledgerTransactionRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> listTransactions(Long accountId, TransactionType type) {
        List<LedgerTransaction> transactions;
        if (accountId != null && type != null) {
            transactions = ledgerTransactionRepository.findByAccountIdAndTypeOrderByCreatedAtDesc(accountId, type);
        } else if (accountId != null) {
            transactions = ledgerTransactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
        } else if (type != null) {
            transactions = ledgerTransactionRepository.findByTypeOrderByCreatedAtDesc(type);
        } else {
            transactions = ledgerTransactionRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        return transactions.stream()
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(Long id) {
        LedgerTransaction transaction = ledgerTransactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found: " + id));
        return toTransactionResponse(transaction);
    }

    @Transactional(readOnly = true)
    public List<LedgerEntryResponse> listEntriesByAccount(Long accountId) {
        accountService.findEntity(accountId);
        return ledgerEntryRepository.findByAccountIdOrderByCreatedAtAscIdAsc(accountId).stream()
                .map(LedgerEntryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransactionResponse toTransactionResponse(LedgerTransaction transaction) {
        List<LedgerEntryResponse> entries = ledgerEntryRepository.findByTransactionIdOrderByCreatedAtAscIdAsc(transaction.getId())
                .stream()
                .map(LedgerEntryResponse::from)
                .collect(Collectors.toList());
        return TransactionResponse.of(transaction, entries);
    }
}
