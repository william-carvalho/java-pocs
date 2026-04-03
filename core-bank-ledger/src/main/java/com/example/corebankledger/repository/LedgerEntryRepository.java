package com.example.corebankledger.repository;

import com.example.corebankledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry> findByAccountIdOrderByCreatedAtAscIdAsc(Long accountId);

    List<LedgerEntry> findByTransactionIdOrderByCreatedAtAscIdAsc(Long transactionId);
}

