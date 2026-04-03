package com.example.logisticfreightsystem.dto;

import com.example.logisticfreightsystem.enums.TransportType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FreightCalculationRequest {

    @NotNull(message = "transportType is required")
    private TransportType transportType;

    @NotNull(message = "width is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "width must be greater than zero")
    private BigDecimal width;

    @NotNull(message = "height is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "height must be greater than zero")
    private BigDecimal height;

    @NotNull(message = "length is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "length must be greater than zero")
    private BigDecimal length;

    @NotNull(message = "weight is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "weight must be greater than zero")
    private BigDecimal weight;

    @NotNull(message = "referenceDate is required")
    private LocalDate referenceDate;

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
}
