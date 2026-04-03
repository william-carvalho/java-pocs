package com.example.logisticfreightsystem.service;

import com.example.logisticfreightsystem.dto.FreightCalculationRequest;
import com.example.logisticfreightsystem.dto.FreightCalculationResponse;
import com.example.logisticfreightsystem.entity.FreightPricingRule;
import com.example.logisticfreightsystem.exception.RuleNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class FreightCalculationService {

    private static final MathContext MATH_CONTEXT = new MathContext(12, RoundingMode.HALF_UP);

    private final FreightPricingRuleService freightPricingRuleService;

    public FreightCalculationService(FreightPricingRuleService freightPricingRuleService) {
        this.freightPricingRuleService = freightPricingRuleService;
    }

    @Transactional(readOnly = true)
    public FreightCalculationResponse calculate(FreightCalculationRequest request) {
        FreightPricingRule rule = freightPricingRuleService.findValidRule(request.getTransportType(), request.getReferenceDate());
        if (rule == null) {
            throw new RuleNotFoundException("No pricing rule found for transport type " + request.getTransportType() +
                    " on date " + request.getReferenceDate());
        }

        BigDecimal volume = request.getWidth()
                .multiply(request.getHeight(), MATH_CONTEXT)
                .multiply(request.getLength(), MATH_CONTEXT);

        BigDecimal volumeCost = volume.multiply(rule.getPricePerVolumeUnit(), MATH_CONTEXT);
        BigDecimal weightCost = request.getWeight().multiply(rule.getPricePerWeightUnit(), MATH_CONTEXT);
        BigDecimal subtotal = rule.getBasePrice().add(volumeCost, MATH_CONTEXT).add(weightCost, MATH_CONTEXT);
        BigDecimal finalPrice = subtotal.multiply(rule.getSizeMultiplier(), MATH_CONTEXT).setScale(2, RoundingMode.HALF_UP);

        FreightCalculationResponse response = new FreightCalculationResponse();
        response.setTransportType(request.getTransportType());
        response.setWidth(request.getWidth());
        response.setHeight(request.getHeight());
        response.setLength(request.getLength());
        response.setWeight(request.getWeight());
        response.setReferenceDate(request.getReferenceDate());
        response.setVolume(volume.stripTrailingZeros());
        response.setAppliedRuleId(rule.getId());
        response.setBasePrice(rule.getBasePrice().stripTrailingZeros());
        response.setPricePerVolumeUnit(rule.getPricePerVolumeUnit().stripTrailingZeros());
        response.setPricePerWeightUnit(rule.getPricePerWeightUnit().stripTrailingZeros());
        response.setSizeMultiplier(rule.getSizeMultiplier().stripTrailingZeros());
        response.setFinalPrice(finalPrice);
        return response;
    }
}
