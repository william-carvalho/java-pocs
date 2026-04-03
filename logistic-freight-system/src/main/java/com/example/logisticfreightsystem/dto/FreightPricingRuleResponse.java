package com.example.logisticfreightsystem.dto;

import com.example.logisticfreightsystem.enums.TransportType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FreightPricingRuleResponse {

    private Long id;
    private TransportType transportType;
    private BigDecimal basePrice;
    private BigDecimal pricePerVolumeUnit;
    private BigDecimal pricePerWeightUnit;
    private BigDecimal sizeMultiplier;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getPricePerVolumeUnit() {
        return pricePerVolumeUnit;
    }

    public void setPricePerVolumeUnit(BigDecimal pricePerVolumeUnit) {
        this.pricePerVolumeUnit = pricePerVolumeUnit;
    }

    public BigDecimal getPricePerWeightUnit() {
        return pricePerWeightUnit;
    }

    public void setPricePerWeightUnit(BigDecimal pricePerWeightUnit) {
        this.pricePerWeightUnit = pricePerWeightUnit;
    }

    public BigDecimal getSizeMultiplier() {
        return sizeMultiplier;
    }

    public void setSizeMultiplier(BigDecimal sizeMultiplier) {
        this.sizeMultiplier = sizeMultiplier;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }
}
