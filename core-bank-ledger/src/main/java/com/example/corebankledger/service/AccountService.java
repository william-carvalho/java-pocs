package com.example.corebankledger.service;

import com.example.corebankledger.dto.AccountResponse;
import com.example.corebankledger.dto.BalanceResponse;
import com.example.corebankledger.dto.CreateAccountRequest;
import com.example.corebankledger.entity.Account;
import com.example.corebankledger.entity.AccountStatus;
import com.example.corebankledger.exception.BusinessException;
import com.example.corebankledger.exception.NotFoundException;
import com.example.corebankledger.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        Account account = new Account();
        account.setOwnerName(request.getOwnerName().trim());
        account.setType(request.getType());
        account.setStatus(AccountStatus.ACTIVE);
        account.setCurrentBalance(MoneyUtils.scale(BigDecimal.ZERO));
        account.setAccountNumber(nextAccountNumber());
        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> listAll() {
        return accountRepository.findAll().stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountResponse getById(Long id) {
        return AccountResponse.from(findEntity(id));
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(Long id) {
        Account account = findEntity(id);
        return new BalanceResponse(account.getId(), account.getAccountNumber(), account.getCurrentBalance());
    }

    @Transactional
    public AccountResponse updateStatus(Long id, AccountStatus status) {
        Account account = findEntity(id);
        account.setStatus(status);
        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public Account findEntity(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found: " + id));
    }

    public void ensureActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("Account is not active: " + account.getId());
        }
    }

    private String nextAccountNumber() {
        return String.format("ACC-%06d", accountRepository.count() + 1);
    }
}
