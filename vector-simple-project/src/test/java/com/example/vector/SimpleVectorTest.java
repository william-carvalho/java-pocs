package com.example.vector;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleVectorTest {
    @Test
    void addsAndReadsValues() {
        SimpleVector<String> vector = new SimpleVector<String>();
        vector.add("Java");
        vector.add("Go");

        assertEquals("Java", vector.get(0));
        assertEquals("Go", vector.get(1));
        assertEquals(2, vector.size());
    }

    @Test
    void growsWhenInitialCapacityIsFull() {
        SimpleVector<Integer> vector = new SimpleVector<Integer>();
        for (int value = 0; value < 10; value++) {
            vector.add(value);
        }

        assertEquals(10, vector.size());
        assertTrue(vector.capacity() >= 10);
        assertEquals(Integer.valueOf(9), vector.get(9));
    }

    @Test
    void setsAndRemovesValues() {
        SimpleVector<String> vector = new SimpleVector<String>();
        vector.add("A");
        vector.add("B");
        vector.add("C");

        assertEquals("B", vector.set(1, "New B"));
        assertEquals("A", vector.remove(0));
        assertEquals("New B", vector.get(0));
        assertEquals(2, vector.size());
    }

    @Test
    void checksWhetherAValueExists() {
        SimpleVector<String> vector = new SimpleVector<String>();
        vector.add("Java");
        vector.add(null);

        assertTrue(vector.contains("Java"));
        assertTrue(vector.contains(null));
        assertFalse(vector.contains("Rust"));
    }

    @Test
    void rejectsInvalidIndexes() {
        SimpleVector<String> vector = new SimpleVector<String>();

        assertThrows(IndexOutOfBoundsException.class, () -> vector.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> vector.remove(-1));
    }
}
