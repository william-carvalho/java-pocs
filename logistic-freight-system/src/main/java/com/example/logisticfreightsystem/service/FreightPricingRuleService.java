package com.example.logisticfreightsystem.service;

import com.example.logisticfreightsystem.dto.FreightPricingRuleRequest;
import com.example.logisticfreightsystem.dto.FreightPricingRuleResponse;
import com.example.logisticfreightsystem.entity.FreightPricingRule;
import com.example.logisticfreightsystem.enums.TransportType;
import com.example.logisticfreightsystem.exception.BusinessValidationException;
import com.example.logisticfreightsystem.repository.FreightPricingRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FreightPricingRuleService {

    private final FreightPricingRuleRepository repository;

    public FreightPricingRuleService(FreightPricingRuleRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FreightPricingRuleResponse createRule(FreightPricingRuleRequest request) {
        validateDateRange(request.getEffectiveFrom(), request.getEffectiveTo());
        validateOverlap(request.getTransportType(), request.getEffectiveFrom(), request.getEffectiveTo());

        FreightPricingRule entity = new FreightPricingRule();
        entity.setTransportType(request.getTransportType());
        entity.setBasePrice(request.getBasePrice());
        entity.setPricePerVolumeUnit(request.getPricePerVolumeUnit());
        entity.setPricePerWeightUnit(request.getPricePerWeightUnit());
        entity.setSizeMultiplier(request.getSizeMultiplier());
        entity.setEffectiveFrom(request.getEffectiveFrom());
        entity.setEffectiveTo(request.getEffectiveTo());

        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<FreightPricingRuleResponse> listRules(TransportType transportType, LocalDate referenceDate) {
        return repository.findAllByFilters(transportType, referenceDate)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FreightPricingRule findValidRule(TransportType transportType, LocalDate referenceDate) {
        return repository.findFirstByTransportTypeAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
                        transportType, referenceDate, referenceDate)
                .orElse(null);
    }

    private void validateDateRange(LocalDate effectiveFrom, LocalDate effectiveTo) {
        if (effectiveFrom.isAfter(effectiveTo)) {
            throw new BusinessValidationException("effectiveFrom must be less than or equal to effectiveTo");
        }
    }

    private void validateOverlap(TransportType transportType, LocalDate effectiveFrom, LocalDate effectiveTo) {
        if (repository.existsOverlappingRule(transportType, effectiveFrom, effectiveTo)) {
            throw new BusinessValidationException("There is already a pricing rule for this transport type in the informed period");
        }
    }

    private FreightPricingRuleResponse toResponse(FreightPricingRule entity) {
        FreightPricingRuleResponse response = new FreightPricingRuleResponse();
        response.setId(entity.getId());
        response.setTransportType(entity.getTransportType());
        response.setBasePrice(entity.getBasePrice());
        response.setPricePerVolumeUnit(entity.getPricePerVolumeUnit());
        response.setPricePerWeightUnit(entity.getPricePerWeightUnit());
        response.setSizeMultiplier(entity.getSizeMultiplier());
        response.setEffectiveFrom(entity.getEffectiveFrom());
        response.setEffectiveTo(entity.getEffectiveTo());
        return response;
    }
}
