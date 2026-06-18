# Tax System Single Class

Java 8 POC for calculating taxes where different products have different tax rates per state and year.

The production code is intentionally in one class:

```text
src/main/java/com/example/tax/TaxSystem.java
```

## Rules

- A tax rule is unique by `productCode + state + year`.
- The same product can have different rates by state.
- The same product and state can have different rates by year.
- Different products can have different rates in the same state and year.
- Calculation formula:

```text
taxValue = baseAmount * taxPercent
totalAmount = baseAmount + taxValue
```

## Example

```java
TaxSystem taxSystem = TaxSystem.withDefaultRules();

TaxSystem.TaxCalculation calculation =
        taxSystem.calculate("PRODUCT_A", "SP", 2024, "1000");
```

## Test

```bash
mvn test
```
