package com.example.guitarfactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class GuitarFactory {
    private final Map<String, GuitarModel> modelsByName = new LinkedHashMap<String, GuitarModel>();
    private final Map<String, Component> componentsByName = new LinkedHashMap<String, Component>();
    private final List<CustomGuitar> customGuitars = new ArrayList<CustomGuitar>();
    private final List<WorkOrder> workOrders = new ArrayList<WorkOrder>();
    private long nextGuitarId = 1;
    private long nextWorkOrderId = 1;

    public GuitarModel addModel(String name, String description, String baseSpecs, String basePrice) {
        GuitarModel model = new GuitarModel(name, description, baseSpecs, money(basePrice));
        modelsByName.put(model.getName(), model);
        return model;
    }

    public Component addComponent(String name, ComponentCategory category, String specification, int quantityInStock, String unitPrice) {
        Component component = new Component(name, category, specification, quantityInStock, money(unitPrice));
        componentsByName.put(component.getName(), component);
        return component;
    }

    public CustomGuitar createCustomGuitar(String customerName, String modelName, SelectedComponent... selectedComponents) {
        if (isBlank(customerName)) {
            throw new IllegalArgumentException("customerName is required");
        }
        GuitarModel model = modelsByName.get(trim(modelName));
        if (model == null) {
            throw new IllegalArgumentException("Unknown guitar model: " + modelName);
        }
        if (selectedComponents == null || selectedComponents.length == 0) {
            throw new IllegalArgumentException("custom guitar requires at least one component");
        }

        List<CustomSpec> specs = new ArrayList<CustomSpec>();
        for (SelectedComponent selected : selectedComponents) {
            Objects.requireNonNull(selected, "selected component");
            Component component = componentsByName.get(selected.componentName);
            if (component == null) {
                throw new IllegalArgumentException("Unknown component: " + selected.componentName);
            }
            component.reserve(selected.quantity);
            specs.add(new CustomSpec(component, selected.quantity));
        }

        CustomGuitar guitar = new CustomGuitar(nextGuitarId++, customerName.trim(), model, specs);
        customGuitars.add(guitar);
        return guitar;
    }

    public WorkOrder createWorkOrder(long customGuitarId) {
        CustomGuitar guitar = findCustomGuitar(customGuitarId);
        if (guitar.getStatus() == CustomGuitarStatus.CANCELLED) {
            throw new IllegalStateException("Cannot create a work order for a cancelled guitar");
        }
        if (guitar.getWorkOrderNumber() != null) {
            throw new IllegalStateException("Guitar already has a work order");
        }

        WorkOrder workOrder = new WorkOrder(nextWorkOrderId++, guitar);
        guitar.attachWorkOrder(workOrder.getNumber());
        workOrders.add(workOrder);
        return workOrder;
    }

    public void cancelWorkOrder(long workOrderId) {
        WorkOrder workOrder = findWorkOrder(workOrderId);
        if (workOrder.getStatus() == WorkOrderStatus.CANCELLED) {
            return;
        }
        if (workOrder.getStatus() == WorkOrderStatus.FINISHED) {
            throw new IllegalStateException("Finished work orders cannot be cancelled");
        }

        workOrder.cancel();
        workOrder.getCustomGuitar().cancel();
        for (CustomSpec spec : workOrder.getCustomGuitar().getSpecs()) {
            spec.getComponent().returnToStock(spec.getQuantity());
        }
    }

    public WorkOrder findWorkOrder(long id) {
        for (WorkOrder workOrder : workOrders) {
            if (workOrder.getId() == id) {
                return workOrder;
            }
        }
        throw new IllegalArgumentException("Unknown work order: " + id);
    }

    public CustomGuitar findCustomGuitar(long id) {
        for (CustomGuitar guitar : customGuitars) {
            if (guitar.getId() == id) {
                return guitar;
            }
        }
        throw new IllegalArgumentException("Unknown custom guitar: " + id);
    }

    public Map<String, Integer> inventory() {
        Map<String, Integer> inventory = new LinkedHashMap<String, Integer>();
        for (Component component : componentsByName.values()) {
            inventory.put(component.getName(), component.getQuantityInStock());
        }
        return Collections.unmodifiableMap(inventory);
    }

    public List<GuitarModel> models() {
        return Collections.unmodifiableList(new ArrayList<GuitarModel>(modelsByName.values()));
    }

    public List<Component> components() {
        return Collections.unmodifiableList(new ArrayList<Component>(componentsByName.values()));
    }

    public List<CustomGuitar> customGuitars() {
        return Collections.unmodifiableList(customGuitars);
    }

    public List<WorkOrder> workOrders() {
        return Collections.unmodifiableList(workOrders);
    }

    public static SelectedComponent select(String componentName, int quantity) {
        return new SelectedComponent(componentName, quantity);
    }

    public static GuitarFactory withDefaultCatalog() {
        GuitarFactory factory = new GuitarFactory();
        factory.addModel("Strat Style", "Comfort contour custom platform", "25.5 scale, bolt-on neck", "2500.00");
        factory.addModel("Tele Style", "Classic single-cut custom platform", "25.5 scale, slab body", "2300.00");
        factory.addModel("Les Paul Style", "Set-neck carved-top platform", "24.75 scale, set neck", "3200.00");
        factory.addComponent("Alder Body", ComponentCategory.BODY_WOOD, "Alder", 10, "500.00");
        factory.addComponent("Maple Neck", ComponentCategory.NECK_WOOD, "Maple", 10, "700.00");
        factory.addComponent("Single Coil Pickup", ComponentCategory.PICKUP, "Vintage single coil", 30, "300.00");
        factory.addComponent("Humbucker Pickup", ComponentCategory.PICKUP, "High output humbucker", 20, "450.00");
        factory.addComponent("Vintage Bridge", ComponentCategory.BRIDGE, "Six-saddle vintage bridge", 15, "350.00");
        factory.addComponent("Black Finish", ComponentCategory.FINISH, "Gloss black", 20, "150.00");
        factory.addComponent("Standard Strings", ComponentCategory.STRINGS, "10-46 nickel strings", 50, "50.00");
        return factory;
    }

    private static BigDecimal money(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }

    private static String trim(String text) {
        return text == null ? null : text.trim();
    }

    private static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    public enum ComponentCategory {
        BODY_WOOD,
        NECK_WOOD,
        PICKUP,
        BRIDGE,
        FINISH,
        STRINGS,
        TUNER
    }

    public enum CustomGuitarStatus {
        CREATED,
        IN_PRODUCTION,
        FINISHED,
        CANCELLED
    }

    public enum WorkOrderStatus {
        OPEN,
        IN_PROGRESS,
        FINISHED,
        CANCELLED
    }

    public static final class GuitarModel {
        private final String name;
        private final String description;
        private final String baseSpecs;
        private final BigDecimal basePrice;

        private GuitarModel(String name, String description, String baseSpecs, BigDecimal basePrice) {
            if (isBlank(name)) {
                throw new IllegalArgumentException("model name is required");
            }
            if (basePrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("basePrice must be zero or greater");
            }
            this.name = name.trim();
            this.description = description == null ? "" : description.trim();
            this.baseSpecs = baseSpecs == null ? "" : baseSpecs.trim();
            this.basePrice = basePrice;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getBaseSpecs() {
            return baseSpecs;
        }

        public BigDecimal getBasePrice() {
            return basePrice;
        }
    }

    public static final class Component {
        private final String name;
        private final ComponentCategory category;
        private final String specification;
        private int quantityInStock;
        private final BigDecimal unitPrice;

        private Component(String name, ComponentCategory category, String specification, int quantityInStock, BigDecimal unitPrice) {
            if (isBlank(name)) {
                throw new IllegalArgumentException("component name is required");
            }
            if (quantityInStock < 0) {
                throw new IllegalArgumentException("quantityInStock must be zero or greater");
            }
            if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("unitPrice must be zero or greater");
            }
            this.name = name.trim();
            this.category = Objects.requireNonNull(category, "category");
            this.specification = specification == null ? "" : specification.trim();
            this.quantityInStock = quantityInStock;
            this.unitPrice = unitPrice;
        }

        private void reserve(int quantity) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be greater than zero");
            }
            if (quantityInStock < quantity) {
                throw new IllegalStateException("Not enough inventory for " + name);
            }
            quantityInStock -= quantity;
        }

        private void returnToStock(int quantity) {
            quantityInStock += quantity;
        }

        public String getName() {
            return name;
        }

        public ComponentCategory getCategory() {
            return category;
        }

        public String getSpecification() {
            return specification;
        }

        public int getQuantityInStock() {
            return quantityInStock;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }
    }

    public static final class SelectedComponent {
        private final String componentName;
        private final int quantity;

        private SelectedComponent(String componentName, int quantity) {
            if (isBlank(componentName)) {
                throw new IllegalArgumentException("componentName is required");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be greater than zero");
            }
            this.componentName = componentName.trim();
            this.quantity = quantity;
        }
    }

    public static final class CustomSpec {
        private final Component component;
        private final int quantity;

        private CustomSpec(Component component, int quantity) {
            this.component = component;
            this.quantity = quantity;
        }

        public Component getComponent() {
            return component;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getDescription() {
            return component.getCategory() + ": " + component.getSpecification();
        }

        public BigDecimal getTotalPrice() {
            return component.getUnitPrice().multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public static final class CustomGuitar {
        private final long id;
        private final String customerName;
        private final GuitarModel model;
        private final List<CustomSpec> specs;
        private CustomGuitarStatus status = CustomGuitarStatus.CREATED;
        private String workOrderNumber;

        private CustomGuitar(long id, String customerName, GuitarModel model, List<CustomSpec> specs) {
            this.id = id;
            this.customerName = customerName;
            this.model = model;
            this.specs = Collections.unmodifiableList(new ArrayList<CustomSpec>(specs));
        }

        private void attachWorkOrder(String workOrderNumber) {
            this.workOrderNumber = workOrderNumber;
            this.status = CustomGuitarStatus.IN_PRODUCTION;
        }

        private void finish() {
            this.status = CustomGuitarStatus.FINISHED;
        }

        private void cancel() {
            this.status = CustomGuitarStatus.CANCELLED;
        }

        public long getId() {
            return id;
        }

        public String getCustomerName() {
            return customerName;
        }

        public GuitarModel getModel() {
            return model;
        }

        public List<CustomSpec> getSpecs() {
            return specs;
        }

        public CustomGuitarStatus getStatus() {
            return status;
        }

        public String getWorkOrderNumber() {
            return workOrderNumber;
        }

        public BigDecimal getTotalPrice() {
            BigDecimal total = model.getBasePrice();
            for (CustomSpec spec : specs) {
                total = total.add(spec.getTotalPrice());
            }
            return total.setScale(2, RoundingMode.HALF_UP);
        }
    }

    public static final class WorkOrder {
        private final long id;
        private final String number;
        private final CustomGuitar customGuitar;
        private WorkOrderStatus status = WorkOrderStatus.OPEN;

        private WorkOrder(long id, CustomGuitar customGuitar) {
            this.id = id;
            this.number = String.format("WO-%05d", id);
            this.customGuitar = customGuitar;
        }

        public void start() {
            if (status != WorkOrderStatus.OPEN) {
                throw new IllegalStateException("Only open work orders can be started");
            }
            status = WorkOrderStatus.IN_PROGRESS;
        }

        public void finish() {
            if (status == WorkOrderStatus.CANCELLED) {
                throw new IllegalStateException("Cancelled work orders cannot be finished");
            }
            status = WorkOrderStatus.FINISHED;
            customGuitar.finish();
        }

        private void cancel() {
            status = WorkOrderStatus.CANCELLED;
        }

        public long getId() {
            return id;
        }

        public String getNumber() {
            return number;
        }

        public CustomGuitar getCustomGuitar() {
            return customGuitar;
        }

        public WorkOrderStatus getStatus() {
            return status;
        }
    }
}
