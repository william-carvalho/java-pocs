package com.example.guitarfactory;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static com.example.guitarfactory.GuitarFactory.ComponentCategory.TUNER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GuitarFactoryTest {
    @Test
    void createsCustomGuitarWithModelSpecsAndTotalPrice() {
        GuitarFactory factory = GuitarFactory.withDefaultCatalog();

        GuitarFactory.CustomGuitar guitar = factory.createCustomGuitar(
                "William",
                "Strat Style",
                GuitarFactory.select("Alder Body", 1),
                GuitarFactory.select("Maple Neck", 1),
                GuitarFactory.select("Single Coil Pickup", 2),
                GuitarFactory.select("Black Finish", 1));

        assertEquals("William", guitar.getCustomerName());
        assertEquals("Strat Style", guitar.getModel().getName());
        assertEquals("25.5 scale, bolt-on neck", guitar.getModel().getBaseSpecs());
        assertEquals(4, guitar.getSpecs().size());
        assertEquals("BODY_WOOD: Alder", guitar.getSpecs().get(0).getDescription());
        assertEquals(new BigDecimal("4450.00"), guitar.getTotalPrice());
        assertEquals(GuitarFactory.CustomGuitarStatus.CREATED, guitar.getStatus());
    }

    @Test
    void debitsInventoryWhenCustomGuitarIsCreated() {
        GuitarFactory factory = GuitarFactory.withDefaultCatalog();

        factory.createCustomGuitar(
                "Ana",
                "Tele Style",
                GuitarFactory.select("Vintage Bridge", 1),
                GuitarFactory.select("Standard Strings", 2));

        Map<String, Integer> inventory = factory.inventory();

        assertEquals(Integer.valueOf(14), inventory.get("Vintage Bridge"));
        assertEquals(Integer.valueOf(48), inventory.get("Standard Strings"));
    }

    @Test
    void createsWorkOrderForCustomGuitar() {
        GuitarFactory factory = GuitarFactory.withDefaultCatalog();
        GuitarFactory.CustomGuitar guitar = factory.createCustomGuitar(
                "Ana",
                "Les Paul Style",
                GuitarFactory.select("Humbucker Pickup", 2));

        GuitarFactory.WorkOrder workOrder = factory.createWorkOrder(guitar.getId());

        assertEquals("WO-00001", workOrder.getNumber());
        assertEquals(GuitarFactory.WorkOrderStatus.OPEN, workOrder.getStatus());
        assertEquals(GuitarFactory.CustomGuitarStatus.IN_PRODUCTION, guitar.getStatus());
        assertEquals("WO-00001", guitar.getWorkOrderNumber());
    }

    @Test
    void tracksWorkOrderLifecycleUntilFinished() {
        GuitarFactory factory = GuitarFactory.withDefaultCatalog();
        GuitarFactory.CustomGuitar guitar = factory.createCustomGuitar(
                "Bruno",
                "Strat Style",
                GuitarFactory.select("Alder Body", 1));
        GuitarFactory.WorkOrder workOrder = factory.createWorkOrder(guitar.getId());

        workOrder.start();
        workOrder.finish();

        assertEquals(GuitarFactory.WorkOrderStatus.FINISHED, workOrder.getStatus());
        assertEquals(GuitarFactory.CustomGuitarStatus.FINISHED, guitar.getStatus());
    }

    @Test
    void cancellationReturnsInventoryAndCancelsCustomGuitar() {
        GuitarFactory factory = GuitarFactory.withDefaultCatalog();
        GuitarFactory.CustomGuitar guitar = factory.createCustomGuitar(
                "Carla",
                "Tele Style",
                GuitarFactory.select("Vintage Bridge", 2));
        GuitarFactory.WorkOrder workOrder = factory.createWorkOrder(guitar.getId());

        assertEquals(Integer.valueOf(13), factory.inventory().get("Vintage Bridge"));

        factory.cancelWorkOrder(workOrder.getId());

        assertEquals(GuitarFactory.WorkOrderStatus.CANCELLED, workOrder.getStatus());
        assertEquals(GuitarFactory.CustomGuitarStatus.CANCELLED, guitar.getStatus());
        assertEquals(Integer.valueOf(15), factory.inventory().get("Vintage Bridge"));
    }

    @Test
    void rejectsOrderWhenInventoryIsInsufficient() {
        GuitarFactory factory = GuitarFactory.withDefaultCatalog();

        assertThrows(IllegalStateException.class, () ->
                factory.createCustomGuitar(
                        "Davi",
                        "Strat Style",
                        GuitarFactory.select("Alder Body", 11)));
    }

    @Test
    void allowsAddingCustomModelsAndComponents() {
        GuitarFactory factory = new GuitarFactory();
        factory.addModel("Baritone", "Extended range guitar", "27 scale, fixed bridge", "4000.00");
        factory.addComponent("Locking Tuners", TUNER, "Black locking tuner set", 5, "280.00");

        GuitarFactory.CustomGuitar guitar = factory.createCustomGuitar(
                "Eva",
                "Baritone",
                GuitarFactory.select("Locking Tuners", 1));

        assertEquals("Baritone", guitar.getModel().getName());
        assertEquals(new BigDecimal("4280.00"), guitar.getTotalPrice());
        assertEquals(Integer.valueOf(4), factory.inventory().get("Locking Tuners"));
    }
}
