package com.example.corebankledger.controller;

import com.example.corebankledger.dto.AccountResponse;
import com.example.corebankledger.dto.BalanceResponse;
import com.example.corebankledger.dto.CreateAccountRequest;
import com.example.corebankledger.dto.LedgerEntryResponse;
import com.example.corebankledger.dto.UpdateAccountStatusRequest;
import com.example.corebankledger.service.AccountService;
import com.example.corebankledger.service.LedgerService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final LedgerService ledgerService;

    public AccountController(AccountService accountService, LedgerService ledgerService) {
        this.accountService = accountService;
        this.ledgerService = ledgerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.create(request);
    }

    @GetMapping
    public List<AccountResponse> listAll() {
        return accountService.listAll();
    }

    @GetMapping("/{id}")
    public AccountResponse getById(@PathVariable Long id) {
        return accountService.getById(id);
    }

    @GetMapping("/{id}/balance")
    public BalanceResponse getBalance(@PathVariable Long id) {
        return accountService.getBalance(id);
    }

    @GetMapping("/{id}/entries")
    public List<LedgerEntryResponse> getEntries(@PathVariable Long id) {
        return ledgerService.listEntriesByAccount(id);
    }

    @PatchMapping("/{id}/status")
    public AccountResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateAccountStatusRequest request) {
        return accountService.updateStatus(id, request.getStatus());
    }
}

