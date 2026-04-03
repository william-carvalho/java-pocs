package com.example.logisticfreightsystem.dto;

import com.example.logisticfreightsystem.enums.TransportType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FreightCalculationResponse {

    private TransportType transportType;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal length;
    private BigDecimal weight;
    private LocalDate referenceDate;
    private BigDecimal volume;
    private Long appliedRuleId;
    private BigDecimal basePrice;
    private BigDecimal pricePerVolumeUnit;
    private BigDecimal pricePerWeightUnit;
    private BigDecimal sizeMultiplier;
    private BigDecimal finalPrice;

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public Long getAppliedRuleId() {
        return appliedRuleId;
    }

    public void setAppliedRuleId(Long appliedRuleId) {
        this.appliedRuleId = appliedRuleId;
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

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }
}
