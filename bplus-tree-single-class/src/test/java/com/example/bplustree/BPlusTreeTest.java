package com.example.bplustree;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BPlusTreeTest {
    @Test
    void insertsAndFindsValues() {
        BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>(4);

        assertNull(tree.put(10, "ten"));
        assertNull(tree.put(5, "five"));
        assertNull(tree.put(20, "twenty"));

        assertEquals("ten", tree.get(10));
        assertEquals("five", tree.get(5));
        assertEquals("twenty", tree.get(20));
        assertNull(tree.get(99));
        assertEquals(3, tree.size());
    }

    @Test
    void updatesExistingKeyWithoutChangingSize() {
        BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>(4);

        tree.put(7, "seven");
        assertEquals("seven", tree.put(7, "SEVEN"));

        assertEquals("SEVEN", tree.get(7));
        assertEquals(1, tree.size());
    }

    @Test
    void keepsKeysOrderedAcrossLeafSplits() {
        BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>(4);

        insertNumbers(tree, 10, 20, 5, 6, 12, 30, 7, 17);

        assertEquals(Arrays.asList(5, 6, 7, 10, 12, 17, 20, 30), tree.keys());
        assertEquals("v17", tree.get(17));
        assertTrue(tree.height() > 1);
    }

    @Test
    void handlesEnoughKeysForInternalSplits() {
        BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>(4);

        for (int number = 1; number <= 40; number++) {
            tree.put(number, "v" + number);
        }

        assertEquals(40, tree.size());
        assertEquals("v1", tree.get(1));
        assertEquals("v21", tree.get(21));
        assertEquals("v40", tree.get(40));
        assertEquals(Integer.valueOf(1), tree.firstKey());
        assertEquals(Integer.valueOf(40), tree.lastKey());
        assertTrue(tree.height() >= 3);
    }

    @Test
    void rangeSearchReturnsValuesInKeyOrder() {
        BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>(4);
        insertNumbers(tree, 10, 20, 5, 6, 12, 30, 7, 17);

        assertEquals(Arrays.asList("v6", "v7", "v10", "v12", "v17"), tree.rangeSearch(6, 17));
        assertTrue(tree.rangeSearch(31, 40).isEmpty());
    }

    @Test
    void supportsNullValuesWhileStillFindingKeys() {
        BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>(4);

        tree.put(1, null);

        assertNull(tree.get(1));
        assertTrue(tree.containsKey(1));
        assertFalse(tree.containsKey(2));
    }

    @Test
    void removesKeysAndRebuildsSearchStructure() {
        BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>(4);
        for (int number = 1; number <= 20; number++) {
            tree.put(number, "v" + number);
        }

        assertEquals("v10", tree.remove(10));
        assertEquals("v1", tree.remove(1));
        assertNull(tree.remove(99));

        assertFalse(tree.containsKey(10));
        assertFalse(tree.containsKey(1));
        assertEquals(18, tree.size());
        assertEquals(Integer.valueOf(2), tree.firstKey());
        assertEquals(Integer.valueOf(20), tree.lastKey());
        assertEquals(Arrays.asList("v8", "v9", "v11", "v12"), tree.rangeSearch(8, 12));
    }

    @Test
    void clearResetsTree() {
        BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>(4);
        insertNumbers(tree, 1, 2, 3, 4, 5);

        tree.clear();

        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
        assertEquals(1, tree.height());
        assertTrue(tree.keys().isEmpty());
        assertNull(tree.get(1));
    }

    @Test
    void supportsComparableStringKeys() {
        BPlusTree<String, Integer> tree = new BPlusTree<String, Integer>(3);

        tree.put("delta", 4);
        tree.put("alpha", 1);
        tree.put("charlie", 3);
        tree.put("bravo", 2);

        assertEquals(Arrays.asList("alpha", "bravo", "charlie", "delta"), tree.keys());
        assertEquals(Integer.valueOf(3), tree.get("charlie"));
        assertEquals(Arrays.asList(2, 3, 4), tree.rangeSearch("bravo", "delta"));
    }

    @Test
    void validatesInputsAndEmptyBoundaryCalls() {
        BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>(4);

        assertThrows(IllegalArgumentException.class, () -> new BPlusTree<Integer, String>(2));
        assertThrows(IllegalArgumentException.class, () -> tree.put(null, "x"));
        assertThrows(IllegalArgumentException.class, () -> tree.get(null));
        assertThrows(IllegalArgumentException.class, () -> tree.remove(null));
        assertThrows(IllegalArgumentException.class, () -> tree.rangeSearch(null, 2));
        assertThrows(IllegalArgumentException.class, () -> tree.rangeSearch(2, null));
        assertThrows(IllegalArgumentException.class, () -> tree.rangeSearch(5, 1));
        assertThrows(NoSuchElementException.class, tree::firstKey);
        assertThrows(NoSuchElementException.class, tree::lastKey);
    }

    private void insertNumbers(BPlusTree<Integer, String> tree, int... numbers) {
        for (int number : numbers) {
            tree.put(number, "v" + number);
        }
    }
}
