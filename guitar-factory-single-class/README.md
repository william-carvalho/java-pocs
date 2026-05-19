# Guitar Factory Single Class

Java 8 POC for building custom guitars from models, specs, components, work orders, and inventory.

The production code is intentionally in one class:

```text
src/main/java/com/example/guitarfactory/GuitarFactory.java
```

## Rules

- The customer chooses a guitar model.
- The customer selects components and quantities for the custom specs.
- Inventory is debited when the custom guitar is created.
- The total price is `model base price + selected component totals`.
- A work order number is generated for production.
- Cancelling a work order returns reserved inventory and cancels the guitar.

## Example

```java
GuitarFactory factory = GuitarFactory.withDefaultCatalog();

GuitarFactory.CustomGuitar guitar = factory.createCustomGuitar(
        "William",
        "Strat Style",
        GuitarFactory.select("Alder Body", 1),
        GuitarFactory.select("Maple Neck", 1),
        GuitarFactory.select("Single Coil Pickup", 2));

GuitarFactory.WorkOrder workOrder = factory.createWorkOrder(guitar.getId());
```

## Test

```bash
mvn test
```
