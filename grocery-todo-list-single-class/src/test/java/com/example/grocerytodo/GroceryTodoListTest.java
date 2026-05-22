package com.example.grocerytodo;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.grocerytodo.GroceryTodoList.ItemStatus.DONE;
import static com.example.grocerytodo.GroceryTodoList.ItemStatus.PENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroceryTodoListTest {
    @Test
    void addsItemAsPending() {
        GroceryTodoList list = new GroceryTodoList();

        GroceryTodoList.GroceryItem item = list.addItem("Milk", "2 liters");

        assertEquals(1L, item.getId());
        assertEquals("Milk", item.getName());
        assertEquals("2 liters", item.getQuantity());
        assertEquals(PENDING, item.getStatus());
        assertEquals(1, list.listAll().size());
    }

    @Test
    void removesItem() {
        GroceryTodoList list = new GroceryTodoList();
        GroceryTodoList.GroceryItem milk = list.addItem("Milk", "2 liters");
        list.addItem("Bread", "1 package");

        GroceryTodoList.GroceryItem removed = list.removeItem(milk.getId());

        assertEquals("Milk", removed.getName());
        assertEquals(1, list.listAll().size());
        assertEquals("Bread", list.listAll().get(0).getName());
    }

    @Test
    void marksItemAsDone() {
        GroceryTodoList list = new GroceryTodoList();
        GroceryTodoList.GroceryItem item = list.addItem("Eggs", "12 units");

        GroceryTodoList.GroceryItem done = list.markAsDone(item.getId());

        assertEquals(DONE, done.getStatus());
        assertFalse(done.getUpdatedAt().isBefore(done.getCreatedAt()));
    }

    @Test
    void doItemMovesItemBackToPending() {
        GroceryTodoList list = new GroceryTodoList();
        GroceryTodoList.GroceryItem item = list.addItem("Coffee", "1 bag");
        list.markAsDone(item.getId());

        GroceryTodoList.GroceryItem pending = list.doItem(item.getId());

        assertEquals(PENDING, pending.getStatus());
    }

    @Test
    void redoItemMovesItemBackToPending() {
        GroceryTodoList list = new GroceryTodoList();
        GroceryTodoList.GroceryItem item = list.addItem("Rice", "5 kg");
        list.markAsDone(item.getId());

        GroceryTodoList.GroceryItem pending = list.redoItem(item.getId());

        assertEquals(PENDING, pending.getStatus());
    }

    @Test
    void listAllKeepsInsertionOrderAndIncludesDoneAndPendingItems() {
        GroceryTodoList list = new GroceryTodoList();
        GroceryTodoList.GroceryItem milk = list.addItem("Milk");
        GroceryTodoList.GroceryItem bread = list.addItem("Bread");
        GroceryTodoList.GroceryItem eggs = list.addItem("Eggs");
        list.markAsDone(bread.getId());

        List<GroceryTodoList.GroceryItem> items = list.listAll();

        assertEquals(3, items.size());
        assertEquals(milk.getId(), items.get(0).getId());
        assertEquals(bread.getId(), items.get(1).getId());
        assertEquals(eggs.getId(), items.get(2).getId());
        assertEquals(PENDING, items.get(0).getStatus());
        assertEquals(DONE, items.get(1).getStatus());
        assertEquals(PENDING, items.get(2).getStatus());
    }

    @Test
    void listsItemsByStatus() {
        GroceryTodoList list = GroceryTodoList.withDefaultItems();

        assertEquals(2, list.listByStatus(PENDING).size());
        assertEquals(1, list.listByStatus(DONE).size());
        assertEquals("Eggs", list.listByStatus(DONE).get(0).getName());
    }

    @Test
    void rejectsBlankItemName() {
        GroceryTodoList list = new GroceryTodoList();

        assertThrows(IllegalArgumentException.class, () -> list.addItem("  ", "1 unit"));
    }

    @Test
    void rejectsOperationsForUnknownItem() {
        GroceryTodoList list = new GroceryTodoList();

        assertThrows(IllegalArgumentException.class, () -> list.markAsDone(99));
        assertThrows(IllegalArgumentException.class, () -> list.doItem(99));
        assertThrows(IllegalArgumentException.class, () -> list.redoItem(99));
        assertThrows(IllegalArgumentException.class, () -> list.removeItem(99));
    }

    @Test
    void exposesCreationAndUpdateTimestamps() {
        GroceryTodoList list = new GroceryTodoList();

        GroceryTodoList.GroceryItem item = list.addItem("Apples", "6 units");
        LocalDateTime createdAt = item.getCreatedAt();
        list.markAsDone(item.getId());

        assertEquals(createdAt, item.getCreatedAt());
        assertTrue(item.getUpdatedAt().isAfter(createdAt) || item.getUpdatedAt().isEqual(createdAt));
    }
}
