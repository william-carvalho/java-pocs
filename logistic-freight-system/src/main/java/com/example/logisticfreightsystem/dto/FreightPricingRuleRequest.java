package com.example.logisticfreightsystem.dto;

import com.example.logisticfreightsystem.enums.TransportType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FreightPricingRuleRequest {

    @NotNull(message = "transportType is required")
    private TransportType transportType;

    @NotNull(message = "basePrice is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "basePrice cannot be negative")
    private BigDecimal basePrice;

    @NotNull(message = "pricePerVolumeUnit is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "pricePerVolumeUnit cannot be negative")
    private BigDecimal pricePerVolumeUnit;

    @NotNull(message = "pricePerWeightUnit is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "pricePerWeightUnit cannot be negative")
    private BigDecimal pricePerWeightUnit;

    @NotNull(message = "sizeMultiplier is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "sizeMultiplier must be greater than zero")
    private BigDecimal sizeMultiplier;

    @NotNull(message = "effectiveFrom is required")
    private LocalDate effectiveFrom;

    @NotNull(message = "effectiveTo is required")
    private LocalDate effectiveTo;

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
