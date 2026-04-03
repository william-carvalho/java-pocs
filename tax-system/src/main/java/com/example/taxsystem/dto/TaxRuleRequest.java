package com.example.taxsystem.dto;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class TaxRuleRequest {

    @NotBlank(message = "productCode is required")
    @Size(max = 100, message = "productCode must have at most 100 characters")
    private String productCode;

    @NotBlank(message = "state is required")
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "state must have 2 letters")
    private String state;

    @NotNull(message = "year is required")
    @Min(value = 1900, message = "year must be greater than or equal to 1900")
    @Max(value = 9999, message = "year must be less than or equal to 9999")
    private Integer year;

    @NotNull(message = "taxPercent is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "taxPercent must be greater than 0")
    @DecimalMax(value = "1.0", inclusive = true, message = "taxPercent must be less than or equal to 1")
    @Digits(integer = 1, fraction = 4, message = "taxPercent must have up to 4 decimal places")
    private BigDecimal taxPercent;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }
}
