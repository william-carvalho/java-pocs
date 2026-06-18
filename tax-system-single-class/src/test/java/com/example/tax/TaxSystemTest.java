package com.example.tax;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaxSystemTest {
    @Test
    void calculatesTaxByProductStateAndYear() {
        TaxSystem system = TaxSystem.withDefaultRules();

        TaxSystem.TaxCalculation calculation = system.calculate("PRODUCT_A", "SP", 2024, "1000");

        assertEquals("PRODUCT_A", calculation.getProductCode());
        assertEquals("SP", calculation.getState());
        assertEquals(2024, calculation.getYear());
        assertEquals(new BigDecimal("1000.00"), calculation.getBaseAmount());
        assertEquals(new BigDecimal("0.1800"), calculation.getTaxPercent());
        assertEquals(new BigDecimal("180.00"), calculation.getTaxValue());
        assertEquals(new BigDecimal("1180.00"), calculation.getTotalAmount());
    }

    @Test
    void sameProductCanHaveDifferentTaxByState() {
        TaxSystem system = TaxSystem.withDefaultRules();

        assertEquals(new BigDecimal("162.00"), system.calculate("PRODUCT_A", "SP", 2024, "900").getTaxValue());
        assertEquals(new BigDecimal("180.00"), system.calculate("PRODUCT_A", "RJ", 2024, "900").getTaxValue());
        assertEquals(new BigDecimal("0.1800"), system.findRule("PRODUCT_A", "SP", 2024).getTaxPercent());
        assertEquals(new BigDecimal("0.2000"), system.findRule("PRODUCT_A", "RJ", 2024).getTaxPercent());
    }

    @Test
    void sameProductAndStateCanHaveDifferentTaxByYear() {
        TaxSystem system = new TaxSystem();
        system.addRule("BOOK", "SP", 2024, "0.10");
        system.addRule("BOOK", "SP", 2025, "0.12");

        assertEquals(new BigDecimal("10.00"), system.calculate("BOOK", "SP", 2024, "100").getTaxValue());
        assertEquals(new BigDecimal("12.00"), system.calculate("BOOK", "SP", 2025, "100").getTaxValue());
    }

    @Test
    void differentProductsCanHaveDifferentTaxInSameStateAndYear() {
        TaxSystem system = new TaxSystem();
        system.addRule("FOOD", "SC", 2026, "0.07");
        system.addRule("ELECTRONICS", "SC", 2026, "0.19");

        assertEquals(new BigDecimal("7.00"), system.calculate("FOOD", "SC", 2026, "100").getTaxValue());
        assertEquals(new BigDecimal("19.00"), system.calculate("ELECTRONICS", "SC", 2026, "100").getTaxValue());
    }

    @Test
    void roundsMoneyHalfUp() {
        TaxSystem system = new TaxSystem();
        system.addRule("SERVICE", "MG", 2026, "0.175");

        TaxSystem.TaxCalculation calculation = system.calculate("SERVICE", "MG", 2026, "99.99");

        assertEquals(new BigDecimal("17.50"), calculation.getTaxValue());
        assertEquals(new BigDecimal("117.49"), calculation.getTotalAmount());
    }

    @Test
    void listRulesCanFilterByProductStateAndYear() {
        TaxSystem system = TaxSystem.withDefaultRules();

        assertEquals(5, system.listRules().size());
        assertEquals(2, system.listRules("PRODUCT_A", null, null).size());
        assertEquals(1, system.listRules("PRODUCT_A", "SP", null).size());
        assertEquals(1, system.listRules(null, null, 2025).size());
        assertEquals("PRODUCT_B", system.listRules(null, "SC", 2025).get(0).getProductCode());
    }

    @Test
    void addRuleNormalizesProductAndState() {
        TaxSystem system = new TaxSystem();

        TaxSystem.TaxRule rule = system.addRule(" food ", "sp", 2024, "0.07");

        assertEquals("FOOD", rule.getProductCode());
        assertEquals("SP", rule.getState());
        assertEquals(new BigDecimal("0.0700"), rule.getTaxPercent());
        assertEquals(new BigDecimal("7.00"), system.calculate("food", "sp", 2024, "100").getTaxValue());
    }

    @Test
    void rejectsDuplicateRuleForSameProductStateAndYear() {
        TaxSystem system = new TaxSystem();
        system.addRule("BOOK", "SP", 2024, "0.10");

        assertThrows(IllegalStateException.class, () -> system.addRule("book", "sp", 2024, "0.11"));
    }

    @Test
    void upsertRuleReplacesExistingRule() {
        TaxSystem system = new TaxSystem();
        system.addRule("BOOK", "SP", 2024, "0.10");

        system.upsertRule("BOOK", "SP", 2024, "0.15");

        assertEquals(new BigDecimal("15.00"), system.calculate("BOOK", "SP", 2024, "100").getTaxValue());
        assertEquals(1, system.listRules().size());
    }

    @Test
    void missingRuleThrowsClearException() {
        TaxSystem system = TaxSystem.withDefaultRules();

        TaxSystem.TaxRuleNotFoundException error = assertThrows(
                TaxSystem.TaxRuleNotFoundException.class,
                () -> system.calculate("PRODUCT_X", "SP", 2024, "1000"));

        assertEquals("No tax rule found for productCode=PRODUCT_X, state=SP, year=2024", error.getMessage());
    }

    @Test
    void rejectsInvalidInputs() {
        TaxSystem system = new TaxSystem();

        assertThrows(IllegalArgumentException.class, () -> system.addRule("", "SP", 2024, "0.10"));
        assertThrows(IllegalArgumentException.class, () -> system.addRule("BOOK", "SAO", 2024, "0.10"));
        assertThrows(IllegalArgumentException.class, () -> system.addRule("BOOK", "SP", 0, "0.10"));
        assertThrows(IllegalArgumentException.class, () -> system.addRule("BOOK", "SP", 2024, "-0.01"));
        assertThrows(IllegalArgumentException.class, () -> system.calculate("BOOK", "SP", 2024, "-1"));
    }
}
