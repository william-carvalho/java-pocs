package com.example.logisticfreight;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.example.logisticfreight.LogisticFreight.SizeCategory.LARGE;
import static com.example.logisticfreight.LogisticFreight.SizeCategory.MEDIUM;
import static com.example.logisticfreight.LogisticFreight.SizeCategory.OVERSIZED;
import static com.example.logisticfreight.LogisticFreight.SizeCategory.SMALL;
import static com.example.logisticfreight.LogisticFreight.TransportationType.BOAT;
import static com.example.logisticfreight.LogisticFreight.TransportationType.RAIL;
import static com.example.logisticfreight.LogisticFreight.TransportationType.TRUCK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LogisticFreightTest {
    @Test
    void calculatesBoatFreightByVolumeAndSize() {
        LogisticFreight.FreightCalculator calculator = new LogisticFreight.FreightCalculator(LogisticFreight.defaultPrices());

        BigDecimal price = calculator.calculate(new LogisticFreight.FreightRequest(10, LARGE, BOAT));

        assertEquals(new BigDecimal("218.30"), price);
    }

    @Test
    void calculatesDifferentPricesForEachTransportationType() {
        LogisticFreight.PriceTable prices = LogisticFreight.PriceTable.builder()
                .price(BOAT, "10.00")
                .price(TRUCK, "15.00")
                .price(RAIL, "12.00")
                .build();
        LogisticFreight.FreightCalculator calculator = new LogisticFreight.FreightCalculator(prices);

        assertEquals(new BigDecimal("45.00"), calculator.calculate(new LogisticFreight.FreightRequest(5, SMALL, BOAT)));
        assertEquals(new BigDecimal("75.00"), calculator.calculate(new LogisticFreight.FreightRequest(5, MEDIUM, TRUCK)));
        assertEquals(new BigDecimal("87.00"), calculator.calculate(new LogisticFreight.FreightRequest(5, OVERSIZED, RAIL)));
    }

    @Test
    void recalculatesUsingUpdatedDynamicPrices() {
        LogisticFreight.FreightCalculator calculator = new LogisticFreight.FreightCalculator(
                LogisticFreight.PriceTable.builder()
                        .price(BOAT, "10.00")
                        .price(TRUCK, "20.00")
                        .price(RAIL, "30.00")
                        .build());
        LogisticFreight.FreightRequest request = new LogisticFreight.FreightRequest(2, MEDIUM, TRUCK);

        assertEquals(new BigDecimal("40.00"), calculator.calculate(request));

        calculator.updatePrices(LogisticFreight.PriceTable.builder()
                .price(BOAT, "11.00")
                .price(TRUCK, "25.50")
                .price(RAIL, "31.00")
                .build());

        assertEquals(new BigDecimal("51.00"), calculator.calculate(request));
    }

    @Test
    void rejectsInvalidVolume() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LogisticFreight.FreightRequest(0, MEDIUM, TRUCK);
        });
    }

    @Test
    void requiresAllTransportationPrices() {
        assertThrows(IllegalStateException.class, () -> {
            LogisticFreight.PriceTable.builder()
                    .price(BOAT, "10.00")
                    .price(TRUCK, "20.00")
                    .build();
        });
    }
}
