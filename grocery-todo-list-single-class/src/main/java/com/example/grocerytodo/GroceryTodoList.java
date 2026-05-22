package com.example.grocerytodo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class GroceryTodoList {
    private final Map<Long, GroceryItem> itemsById = new LinkedHashMap<Long, GroceryItem>();
    private long nextId = 1;

    public GroceryItem addItem(String name) {
        return addItem(name, null);
    }

    public GroceryItem addItem(String name, String quantity) {
        GroceryItem item = new GroceryItem(nextId++, name, quantity);
        itemsById.put(item.getId(), item);
        return item;
    }

    public GroceryItem markAsDone(long id) {
        GroceryItem item = findItem(id);
        item.markAsDone();
        return item;
    }

    public GroceryItem doItem(long id) {
        GroceryItem item = findItem(id);
        item.markAsPending();
        return item;
    }

    public GroceryItem redoItem(long id) {
        GroceryItem item = findItem(id);
        item.markAsPending();
        return item;
    }

    public GroceryItem removeItem(long id) {
        GroceryItem removed = itemsById.remove(id);
        if (removed == null) {
            throw new IllegalArgumentException("Item not found: " + id);
        }
        return removed;
    }

    public GroceryItem findItem(long id) {
        GroceryItem item = itemsById.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Item not found: " + id);
        }
        return item;
    }

    public List<GroceryItem> listAll() {
        return Collections.unmodifiableList(new ArrayList<GroceryItem>(itemsById.values()));
    }

    public List<GroceryItem> listByStatus(ItemStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status is required");
        }

        List<GroceryItem> result = new ArrayList<GroceryItem>();
        for (GroceryItem item : itemsById.values()) {
            if (item.getStatus() == status) {
                result.add(item);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public static GroceryTodoList withDefaultItems() {
        GroceryTodoList list = new GroceryTodoList();
        list.addItem("Milk", "2 liters");
        list.addItem("Bread", "1 package");
        list.addItem("Eggs", "12 units").markAsDone();
        return list;
    }

    private static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    public enum ItemStatus {
        PENDING,
        DONE
    }

    public static final class GroceryItem {
        private final long id;
        private final String name;
        private final String quantity;
        private final LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private ItemStatus status;

        private GroceryItem(long id, String name, String quantity) {
            if (isBlank(name)) {
                throw new IllegalArgumentException("name is required");
            }
            this.id = id;
            this.name = name.trim();
            this.quantity = quantity == null ? "" : quantity.trim();
            this.createdAt = LocalDateTime.now();
            this.updatedAt = createdAt;
            this.status = ItemStatus.PENDING;
        }

        private void markAsDone() {
            status = ItemStatus.DONE;
            updatedAt = LocalDateTime.now();
        }

        private void markAsPending() {
            status = ItemStatus.PENDING;
            updatedAt = LocalDateTime.now();
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getQuantity() {
            return quantity;
        }

        public ItemStatus getStatus() {
            return status;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
    }
}
