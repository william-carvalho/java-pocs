package com.example.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class TaxSystem {
    private final Map<TaxRuleKey, TaxRule> rules = new LinkedHashMap<TaxRuleKey, TaxRule>();

    public TaxRule addRule(String productCode, String state, int year, String taxPercent) {
        return addRule(productCode, state, year, new BigDecimal(taxPercent));
    }

    public TaxRule addRule(String productCode, String state, int year, BigDecimal taxPercent) {
        TaxRule rule = new TaxRule(productCode, state, year, taxPercent);
        TaxRuleKey key = rule.key();
        if (rules.containsKey(key)) {
            throw new IllegalStateException("Tax rule already exists for productCode="
                    + rule.getProductCode() + ", state=" + rule.getState() + ", year=" + rule.getYear());
        }
        rules.put(key, rule);
        return rule;
    }

    public TaxRule upsertRule(String productCode, String state, int year, String taxPercent) {
        TaxRule rule = new TaxRule(productCode, state, year, new BigDecimal(taxPercent));
        rules.put(rule.key(), rule);
        return rule;
    }

    public TaxRule findRule(String productCode, String state, int year) {
        TaxRule rule = rules.get(new TaxRuleKey(normalizeProduct(productCode), normalizeState(state), year));
        if (rule == null) {
            throw new TaxRuleNotFoundException(productCode, state, year);
        }
        return rule;
    }

    public TaxCalculation calculate(String productCode, String state, int year, String baseAmount) {
        return calculate(productCode, state, year, new BigDecimal(baseAmount));
    }

    public TaxCalculation calculate(String productCode, String state, int year, BigDecimal baseAmount) {
        if (baseAmount == null || baseAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("baseAmount must be zero or greater");
        }
        TaxRule rule = findRule(productCode, state, year);
        BigDecimal normalizedBase = money(baseAmount);
        BigDecimal taxValue = money(normalizedBase.multiply(rule.getTaxPercent()));
        BigDecimal totalAmount = money(normalizedBase.add(taxValue));
        return new TaxCalculation(
                rule.getProductCode(),
                rule.getState(),
                rule.getYear(),
                normalizedBase,
                rule.getTaxPercent(),
                taxValue,
                totalAmount);
    }

    public List<TaxRule> listRules() {
        return Collections.unmodifiableList(new ArrayList<TaxRule>(rules.values()));
    }

    public List<TaxRule> listRules(String productCode, String state, Integer year) {
        String normalizedProduct = isBlank(productCode) ? null : normalizeProduct(productCode);
        String normalizedState = isBlank(state) ? null : normalizeState(state);
        List<TaxRule> result = new ArrayList<TaxRule>();
        for (TaxRule rule : rules.values()) {
            if (normalizedProduct != null && !rule.getProductCode().equals(normalizedProduct)) {
                continue;
            }
            if (normalizedState != null && !rule.getState().equals(normalizedState)) {
                continue;
            }
            if (year != null && rule.getYear() != year.intValue()) {
                continue;
            }
            result.add(rule);
        }
        return Collections.unmodifiableList(result);
    }

    public static TaxSystem withDefaultRules() {
        TaxSystem system = new TaxSystem();
        system.addRule("PRODUCT_A", "SP", 2024, "0.18");
        system.addRule("PRODUCT_A", "RJ", 2024, "0.20");
        system.addRule("PRODUCT_B", "SC", 2025, "0.12");
        system.addRule("PRODUCT_C", "MG", 2023, "0.15");
        system.addRule("FOOD", "SP", 2024, "0.07");
        return system;
    }

    private static BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal percent(BigDecimal value) {
        return value.setScale(4, RoundingMode.HALF_UP);
    }

    private static String normalizeProduct(String productCode) {
        if (isBlank(productCode)) {
            throw new IllegalArgumentException("productCode is required");
        }
        return productCode.trim().toUpperCase();
    }

    private static String normalizeState(String state) {
        if (isBlank(state)) {
            throw new IllegalArgumentException("state is required");
        }
        String normalized = state.trim().toUpperCase();
        if (normalized.length() != 2) {
            throw new IllegalArgumentException("state must have 2 letters");
        }
        return normalized;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static final class TaxRuleKey {
        private final String productCode;
        private final String state;
        private final int year;

        private TaxRuleKey(String productCode, String state, int year) {
            this.productCode = productCode;
            this.state = state;
            this.year = year;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof TaxRuleKey)) {
                return false;
            }
            TaxRuleKey that = (TaxRuleKey) other;
            return year == that.year
                    && productCode.equals(that.productCode)
                    && state.equals(that.state);
        }

        public int hashCode() {
            return Objects.hash(productCode, state, year);
        }
    }

    public static final class TaxRule {
        private final String productCode;
        private final String state;
        private final int year;
        private final BigDecimal taxPercent;

        private TaxRule(String productCode, String state, int year, BigDecimal taxPercent) {
            if (year <= 0) {
                throw new IllegalArgumentException("year must be greater than zero");
            }
            if (taxPercent == null || taxPercent.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("taxPercent must be zero or greater");
            }
            this.productCode = normalizeProduct(productCode);
            this.state = normalizeState(state);
            this.year = year;
            this.taxPercent = percent(taxPercent);
        }

        private TaxRuleKey key() {
            return new TaxRuleKey(productCode, state, year);
        }

        public String getProductCode() {
            return productCode;
        }

        public String getState() {
            return state;
        }

        public int getYear() {
            return year;
        }

        public BigDecimal getTaxPercent() {
            return taxPercent;
        }
    }

    public static final class TaxCalculation {
        private final String productCode;
        private final String state;
        private final int year;
        private final BigDecimal baseAmount;
        private final BigDecimal taxPercent;
        private final BigDecimal taxValue;
        private final BigDecimal totalAmount;

        private TaxCalculation(String productCode,
                               String state,
                               int year,
                               BigDecimal baseAmount,
                               BigDecimal taxPercent,
                               BigDecimal taxValue,
                               BigDecimal totalAmount) {
            this.productCode = productCode;
            this.state = state;
            this.year = year;
            this.baseAmount = baseAmount;
            this.taxPercent = taxPercent;
            this.taxValue = taxValue;
            this.totalAmount = totalAmount;
        }

        public String getProductCode() {
            return productCode;
        }

        public String getState() {
            return state;
        }

        public int getYear() {
            return year;
        }

        public BigDecimal getBaseAmount() {
            return baseAmount;
        }

        public BigDecimal getTaxPercent() {
            return taxPercent;
        }

        public BigDecimal getTaxValue() {
            return taxValue;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }
    }

    public static final class TaxRuleNotFoundException extends RuntimeException {
        private TaxRuleNotFoundException(String productCode, String state, int year) {
            super("No tax rule found for productCode=" + productCode + ", state=" + state + ", year=" + year);
        }
    }
}
