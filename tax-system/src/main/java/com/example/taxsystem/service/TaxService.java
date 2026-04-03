package com.example.taxsystem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.taxsystem.dto.TaxCalculationRequest;
import com.example.taxsystem.dto.TaxCalculationResponse;
import com.example.taxsystem.entity.TaxRule;

@Service
public class TaxService {

    private final TaxRuleService taxRuleService;

    public TaxService(TaxRuleService taxRuleService) {
        this.taxRuleService = taxRuleService;
    }

    @Transactional(readOnly = true)
    public TaxCalculationResponse calculate(TaxCalculationRequest request) {
        TaxRule taxRule = taxRuleService.getRequiredRule(request.getProductCode(), request.getState(), request.getYear());

        BigDecimal baseAmount = request.getBaseAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxValue = baseAmount.multiply(taxRule.getTaxPercent()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = baseAmount.add(taxValue).setScale(2, RoundingMode.HALF_UP);

        TaxCalculationResponse response = new TaxCalculationResponse();
        response.setProductCode(taxRule.getProductCode());
        response.setState(taxRule.getState());
        response.setYear(taxRule.getYear());
        response.setBaseAmount(baseAmount);
        response.setTaxPercent(taxRule.getTaxPercent());
        response.setTaxValue(taxValue);
        response.setTotalAmount(totalAmount);
        return response;
    }
}
