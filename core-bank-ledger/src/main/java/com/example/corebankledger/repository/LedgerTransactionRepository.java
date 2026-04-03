package com.example.corebankledger.repository;

import com.example.corebankledger.entity.LedgerTransaction;
import com.example.corebankledger.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LedgerTransactionRepository extends JpaRepository<LedgerTransaction, Long> {

    List<LedgerTransaction> findByTypeOrderByCreatedAtDesc(TransactionType type);

    @Query("select distinct t from LedgerTransaction t join LedgerEntry e on e.transaction.id = t.id " +
            "where e.account.id = :accountId order by t.createdAt desc")
    List<LedgerTransaction> findByAccountIdOrderByCreatedAtDesc(@Param("accountId") Long accountId);

    @Query("select distinct t from LedgerTransaction t join LedgerEntry e on e.transaction.id = t.id " +
            "where e.account.id = :accountId and t.type = :type order by t.createdAt desc")
    List<LedgerTransaction> findByAccountIdAndTypeOrderByCreatedAtDesc(@Param("accountId") Long accountId,
                                                                       @Param("type") TransactionType type);
}

