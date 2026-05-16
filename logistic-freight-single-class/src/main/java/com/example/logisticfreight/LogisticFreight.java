package com.example.logisticfreight;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class LogisticFreight {
    private LogisticFreight() {
    }

    public enum TransportationType {
        BOAT,
        TRUCK,
        RAIL
    }

    public enum SizeCategory {
        SMALL(new BigDecimal("0.90")),
        MEDIUM(new BigDecimal("1.00")),
        LARGE(new BigDecimal("1.18")),
        OVERSIZED(new BigDecimal("1.45"));

        private final BigDecimal multiplier;

        SizeCategory(BigDecimal multiplier) {
            this.multiplier = multiplier;
        }
    }

    public static final class FreightRequest {
        private final BigDecimal volumeInCubicMeters;
        private final SizeCategory sizeCategory;
        private final TransportationType transportationType;

        public FreightRequest(double volumeInCubicMeters,
                              SizeCategory sizeCategory,
                              TransportationType transportationType) {
            this(BigDecimal.valueOf(volumeInCubicMeters), sizeCategory, transportationType);
        }

        public FreightRequest(BigDecimal volumeInCubicMeters,
                              SizeCategory sizeCategory,
                              TransportationType transportationType) {
            if (volumeInCubicMeters == null || volumeInCubicMeters.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("volumeInCubicMeters must be greater than zero");
            }
            this.volumeInCubicMeters = volumeInCubicMeters;
            this.sizeCategory = Objects.requireNonNull(sizeCategory, "sizeCategory");
            this.transportationType = Objects.requireNonNull(transportationType, "transportationType");
        }
    }

    public static final class PriceTable {
        private final Map<TransportationType, BigDecimal> pricePerCubicMeter;

        private PriceTable(Map<TransportationType, BigDecimal> pricePerCubicMeter) {
            this.pricePerCubicMeter = Collections.unmodifiableMap(new EnumMap<TransportationType, BigDecimal>(pricePerCubicMeter));
        }

        public BigDecimal priceFor(TransportationType transportationType) {
            BigDecimal price = pricePerCubicMeter.get(transportationType);
            if (price == null) {
                throw new IllegalArgumentException("No price configured for " + transportationType);
            }
            return price;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private final Map<TransportationType, BigDecimal> prices = new EnumMap<TransportationType, BigDecimal>(TransportationType.class);

            public Builder price(TransportationType type, String pricePerCubicMeter) {
                return price(type, new BigDecimal(pricePerCubicMeter));
            }

            public Builder price(TransportationType type, BigDecimal pricePerCubicMeter) {
                Objects.requireNonNull(type, "type");
                if (pricePerCubicMeter == null || pricePerCubicMeter.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("pricePerCubicMeter must be zero or greater");
                }
                prices.put(type, pricePerCubicMeter);
                return this;
            }

            public PriceTable build() {
                for (TransportationType type : TransportationType.values()) {
                    if (!prices.containsKey(type)) {
                        throw new IllegalStateException("Missing price for " + type);
                    }
                }
                return new PriceTable(prices);
            }
        }
    }

    public static final class FreightCalculator {
        private PriceTable currentPrices;

        public FreightCalculator(PriceTable initialPrices) {
            this.currentPrices = Objects.requireNonNull(initialPrices, "initialPrices");
        }

        public void updatePrices(PriceTable newPrices) {
            this.currentPrices = Objects.requireNonNull(newPrices, "newPrices");
        }

        public BigDecimal calculate(FreightRequest request) {
            Objects.requireNonNull(request, "request");

            BigDecimal transportationPrice = currentPrices.priceFor(request.transportationType);
            return request.volumeInCubicMeters
                    .multiply(transportationPrice)
                    .multiply(request.sizeCategory.multiplier)
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }

    public static PriceTable defaultPrices() {
        return PriceTable.builder()
                .price(TransportationType.BOAT, "18.50")
                .price(TransportationType.TRUCK, "27.00")
                .price(TransportationType.RAIL, "22.25")
                .build();
    }
}
