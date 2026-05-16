# Logistic Freight Single Class

Java 8 POC for calculating freight prices by:

- volume in cubic meters
- cargo size category
- transportation type: boat, truck, or rail
- dynamic prices that can be replaced at runtime

The production code is intentionally in one class:

```text
src/main/java/com/example/logisticfreight/LogisticFreight.java
```

## Example

```java
LogisticFreight.FreightCalculator calculator =
        new LogisticFreight.FreightCalculator(LogisticFreight.defaultPrices());

BigDecimal truckPrice = calculator.calculate(
        new LogisticFreight.FreightRequest(
                12.5,
                LogisticFreight.SizeCategory.LARGE,
                LogisticFreight.TransportationType.TRUCK));

calculator.updatePrices(LogisticFreight.PriceTable.builder()
        .price(LogisticFreight.TransportationType.BOAT, "20.00")
        .price(LogisticFreight.TransportationType.TRUCK, "31.50")
        .price(LogisticFreight.TransportationType.RAIL, "24.00")
        .build());
```

## Test

```bash
mvn test
```
