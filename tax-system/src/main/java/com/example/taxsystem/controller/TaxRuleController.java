package com.example.taxsystem.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.taxsystem.dto.TaxRuleRequest;
import com.example.taxsystem.dto.TaxRuleResponse;
import com.example.taxsystem.service.TaxRuleService;

@RestController
@RequestMapping("/tax-rules")
public class TaxRuleController {

    private final TaxRuleService taxRuleService;

    public TaxRuleController(TaxRuleService taxRuleService) {
        this.taxRuleService = taxRuleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaxRuleResponse create(@Valid @RequestBody TaxRuleRequest request) {
        return taxRuleService.create(request);
    }

    @GetMapping
    public List<TaxRuleResponse> findAll(@RequestParam(required = false) String productCode,
                                         @RequestParam(required = false) String state,
                                         @RequestParam(required = false) Integer year) {
        return taxRuleService.findAll(productCode, state, year);
    }
}
