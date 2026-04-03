package com.example.corebankledger;

import com.example.corebankledger.dto.BalanceResponse;
import com.example.corebankledger.dto.CreateAccountRequest;
import com.example.corebankledger.dto.MoneyOperationRequest;
import com.example.corebankledger.dto.TransactionResponse;
import com.example.corebankledger.dto.TransferRequest;
import com.example.corebankledger.entity.AccountStatus;
import com.example.corebankledger.entity.AccountType;
import com.example.corebankledger.exception.BusinessException;
import com.example.corebankledger.service.AccountService;
import com.example.corebankledger.service.LedgerService;
import com.example.corebankledger.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CoreBankLedgerApplicationTests {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private LedgerService ledgerService;

    @Test
    void shouldExecuteCreditTransferDebitAndKeepLedgerConsistent() {
        Long accountA = createAccount("Ledger A", AccountType.CHECKING);
        Long accountB = createAccount("Ledger B", AccountType.SAVINGS);

        transactionService.credit(operation(accountA, "1000.00", "Initial deposit"));
        TransactionResponse transferResponse = transactionService.transfer(transfer(accountA, accountB, "300.00", "Internal transfer"));
        transactionService.debit(operation(accountB, "100.00", "Cash out"));

        BalanceResponse balanceA = accountService.getBalance(accountA);
        BalanceResponse balanceB = accountService.getBalance(accountB);

        assertEquals(new BigDecimal("700.00"), balanceA.getCurrentBalance());
        assertEquals(new BigDecimal("200.00"), balanceB.getCurrentBalance());
        assertEquals(2, transferResponse.getEntries().size());
        assertEquals(2, ledgerService.listEntriesByAccount(accountA).size());
        assertEquals(2, ledgerService.listEntriesByAccount(accountB).size());
    }

    @Test
    void shouldRejectDebitWhenBalanceIsInsufficient() {
        Long accountId = createAccount("No Money", AccountType.CHECKING);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> transactionService.debit(operation(accountId, "10.00", "Debit without funds")));

        assertEquals("Insufficient balance for account: " + accountId, exception.getMessage());
    }

    @Test
    void shouldRejectTransactionForBlockedAccount() {
        Long accountId = createAccount("Blocked Owner", AccountType.CHECKING);
        accountService.updateStatus(accountId, AccountStatus.BLOCKED);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> transactionService.credit(operation(accountId, "20.00", "Blocked credit")));

        assertEquals("Account is not active: " + accountId, exception.getMessage());
    }

    private Long createAccount(String ownerName, AccountType type) {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setOwnerName(ownerName);
        request.setType(type);
        return accountService.create(request).getId();
    }

    private MoneyOperationRequest operation(Long accountId, String amount, String description) {
        MoneyOperationRequest request = new MoneyOperationRequest();
        request.setAccountId(accountId);
        request.setAmount(new BigDecimal(amount));
        request.setDescription(description);
        return request;
    }

    private TransferRequest transfer(Long fromAccountId, Long toAccountId, String amount, String description) {
        TransferRequest request = new TransferRequest();
        request.setFromAccountId(fromAccountId);
        request.setToAccountId(toAccountId);
        request.setAmount(new BigDecimal(amount));
        request.setDescription(description);
        return request;
    }
}

