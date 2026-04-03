package com.example.logisticfreightsystem.entity;

import com.example.logisticfreightsystem.enums.TransportType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "freight_pricing_rules")
public class FreightPricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransportType transportType;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal basePrice;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal pricePerVolumeUnit;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal pricePerWeightUnit;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal sizeMultiplier;

    @Column(nullable = false)
    private LocalDate effectiveFrom;

    @Column(nullable = false)
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
