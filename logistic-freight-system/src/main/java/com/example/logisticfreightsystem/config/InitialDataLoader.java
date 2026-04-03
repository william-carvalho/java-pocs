package com.example.logisticfreightsystem.config;

import com.example.logisticfreightsystem.entity.FreightPricingRule;
import com.example.logisticfreightsystem.enums.TransportType;
import com.example.logisticfreightsystem.repository.FreightPricingRuleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

@Configuration
public class InitialDataLoader {

    @Bean
    public CommandLineRunner loadInitialData(FreightPricingRuleRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            repository.saveAll(Arrays.asList(
                    buildRule(TransportType.TRUCK, "100", "0.02", "0.50", "1.0"),
                    buildRule(TransportType.BOAT, "180", "0.01", "0.30", "1.2"),
                    buildRule(TransportType.RAIL, "140", "0.015", "0.40", "1.1")
            ));
        };
    }

    private FreightPricingRule buildRule(TransportType transportType,
                                         String basePrice,
                                         String pricePerVolumeUnit,
                                         String pricePerWeightUnit,
                                         String sizeMultiplier) {
        FreightPricingRule rule = new FreightPricingRule();
        rule.setTransportType(transportType);
        rule.setBasePrice(new BigDecimal(basePrice));
        rule.setPricePerVolumeUnit(new BigDecimal(pricePerVolumeUnit));
        rule.setPricePerWeightUnit(new BigDecimal(pricePerWeightUnit));
        rule.setSizeMultiplier(new BigDecimal(sizeMultiplier));
        rule.setEffectiveFrom(LocalDate.of(2026, 1, 1));
        rule.setEffectiveTo(LocalDate.of(2026, 6, 30));
        return rule;
    }
}
