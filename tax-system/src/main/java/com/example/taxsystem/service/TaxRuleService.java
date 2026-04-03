package com.example.taxsystem.service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.taxsystem.dto.TaxRuleRequest;
import com.example.taxsystem.dto.TaxRuleResponse;
import com.example.taxsystem.entity.TaxRule;
import com.example.taxsystem.exception.DuplicateTaxRuleException;
import com.example.taxsystem.repository.TaxRuleRepository;

@Service
public class TaxRuleService {

    private final TaxRuleRepository taxRuleRepository;

    public TaxRuleService(TaxRuleRepository taxRuleRepository) {
        this.taxRuleRepository = taxRuleRepository;
    }

    @Transactional
    public TaxRuleResponse create(TaxRuleRequest request) {
        String productCode = normalizeProductCode(request.getProductCode());
        String state = normalizeState(request.getState());

        if (taxRuleRepository.existsByProductCodeAndStateAndYear(productCode, state, request.getYear())) {
            throw new DuplicateTaxRuleException("Tax rule already exists for productCode, state and year");
        }

        TaxRule taxRule = new TaxRule();
        taxRule.setProductCode(productCode);
        taxRule.setState(state);
        taxRule.setYear(request.getYear());
        taxRule.setTaxPercent(request.getTaxPercent());

        return toResponse(taxRuleRepository.save(taxRule));
    }

    @Transactional(readOnly = true)
    public List<TaxRuleResponse> findAll(String productCode, String state, Integer year) {
        return taxRuleRepository.findByFilters(normalizeNullableProductCode(productCode), normalizeNullableState(state), year)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaxRule getRequiredRule(String productCode, String state, Integer year) {
        return taxRuleRepository.findByProductCodeAndStateAndYear(
                normalizeProductCode(productCode),
                normalizeState(state),
                year)
                .orElseThrow(() -> new com.example.taxsystem.exception.ResourceNotFoundException(
                        "No tax rule found for productCode=" + normalizeProductCode(productCode)
                                + ", state=" + normalizeState(state)
                                + ", year=" + year));
    }

    private TaxRuleResponse toResponse(TaxRule taxRule) {
        TaxRuleResponse response = new TaxRuleResponse();
        response.setId(taxRule.getId());
        response.setProductCode(taxRule.getProductCode());
        response.setState(taxRule.getState());
        response.setYear(taxRule.getYear());
        response.setTaxPercent(taxRule.getTaxPercent());
        return response;
    }

    private String normalizeNullableProductCode(String productCode) {
        return productCode == null || productCode.trim().isEmpty() ? null : normalizeProductCode(productCode);
    }

    private String normalizeNullableState(String state) {
        return state == null || state.trim().isEmpty() ? null : normalizeState(state);
    }

    private String normalizeProductCode(String productCode) {
        return productCode.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeState(String state) {
        return state.trim().toUpperCase(Locale.ROOT);
    }
}
