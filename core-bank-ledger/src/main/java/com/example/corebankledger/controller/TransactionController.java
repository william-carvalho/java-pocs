package com.example.corebankledger.controller;

import com.example.corebankledger.dto.MoneyOperationRequest;
import com.example.corebankledger.dto.TransactionResponse;
import com.example.corebankledger.dto.TransferRequest;
import com.example.corebankledger.entity.TransactionType;
import com.example.corebankledger.service.LedgerService;
import com.example.corebankledger.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final LedgerService ledgerService;

    public TransactionController(TransactionService transactionService, LedgerService ledgerService) {
        this.transactionService = transactionService;
        this.ledgerService = ledgerService;
    }

    @PostMapping("/credit")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse credit(@Valid @RequestBody MoneyOperationRequest request) {
        return transactionService.credit(request);
    }

    @PostMapping("/debit")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse debit(@Valid @RequestBody MoneyOperationRequest request) {
        return transactionService.debit(request);
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse transfer(@Valid @RequestBody TransferRequest request) {
        return transactionService.transfer(request);
    }

    @GetMapping
    public List<TransactionResponse> listTransactions(@RequestParam(required = false) Long accountId,
                                                      @RequestParam(required = false) TransactionType type) {
        return ledgerService.listTransactions(accountId, type);
    }

    @GetMapping("/{id}")
    public TransactionResponse getById(@PathVariable Long id) {
        return ledgerService.getTransaction(id);
    }
}

