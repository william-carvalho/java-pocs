package com.example.bst;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BinarySearchTreeTest {
    @Test
    void insertsAndSearchesValues() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<Integer>();

        assertTrue(tree.insert(8));
        assertTrue(tree.insert(3));
        assertTrue(tree.insert(10));
        assertTrue(tree.insert(1));

        assertTrue(tree.contains(8));
        assertTrue(tree.contains(1));
        assertFalse(tree.contains(99));
        assertEquals(4, tree.size());
    }

    @Test
    void duplicateInsertDoesNotIncreaseSize() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<Integer>();

        assertTrue(tree.insert(5));
        assertFalse(tree.insert(5));

        assertEquals(1, tree.size());
    }

    @Test
    void returnsValuesInOrder() {
        BinarySearchTree<Integer> tree = sampleTree();

        assertEquals(Arrays.asList(1, 3, 4, 6, 7, 8, 10, 13, 14), tree.inOrder());
    }

    @Test
    void returnsPreOrderAndPostOrder() {
        BinarySearchTree<Integer> tree = sampleTree();

        assertEquals(Arrays.asList(8, 3, 1, 6, 4, 7, 10, 14, 13), tree.preOrder());
        assertEquals(Arrays.asList(1, 4, 7, 6, 3, 13, 14, 10, 8), tree.postOrder());
    }

    @Test
    void returnsMinMaxAndHeight() {
        BinarySearchTree<Integer> tree = sampleTree();

        assertEquals(Integer.valueOf(1), tree.min());
        assertEquals(Integer.valueOf(14), tree.max());
        assertEquals(4, tree.height());
    }

    @Test
    void removesLeafNode() {
        BinarySearchTree<Integer> tree = sampleTree();

        assertTrue(tree.remove(1));

        assertFalse(tree.contains(1));
        assertEquals(Arrays.asList(3, 4, 6, 7, 8, 10, 13, 14), tree.inOrder());
        assertEquals(8, tree.size());
    }

    @Test
    void removesNodeWithOneChild() {
        BinarySearchTree<Integer> tree = sampleTree();

        assertTrue(tree.remove(14));

        assertFalse(tree.contains(14));
        assertTrue(tree.contains(13));
        assertEquals(Arrays.asList(1, 3, 4, 6, 7, 8, 10, 13), tree.inOrder());
    }

    @Test
    void removesNodeWithTwoChildren() {
        BinarySearchTree<Integer> tree = sampleTree();

        assertTrue(tree.remove(3));

        assertFalse(tree.contains(3));
        assertEquals(Arrays.asList(1, 4, 6, 7, 8, 10, 13, 14), tree.inOrder());
        assertEquals(8, tree.size());
    }

    @Test
    void removesRootWithTwoChildren() {
        BinarySearchTree<Integer> tree = sampleTree();

        assertTrue(tree.remove(8));

        assertFalse(tree.contains(8));
        assertEquals(Arrays.asList(1, 3, 4, 6, 7, 10, 13, 14), tree.inOrder());
        assertEquals(8, tree.size());
    }

    @Test
    void removeMissingValueReturnsFalse() {
        BinarySearchTree<Integer> tree = sampleTree();

        assertFalse(tree.remove(99));
        assertEquals(9, tree.size());
    }

    @Test
    void supportsComparableTypes() {
        BinarySearchTree<String> tree = new BinarySearchTree<String>();

        tree.insert("delta");
        tree.insert("alpha");
        tree.insert("charlie");

        assertEquals(Arrays.asList("alpha", "charlie", "delta"), tree.inOrder());
        assertEquals("alpha", tree.min());
        assertEquals("delta", tree.max());
    }

    @Test
    void clearEmptiesTree() {
        BinarySearchTree<Integer> tree = sampleTree();

        tree.clear();

        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
        assertEquals(0, tree.height());
        assertTrue(tree.inOrder().isEmpty());
    }

    @Test
    void validatesNullValuesAndEmptyMinMax() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<Integer>();

        assertThrows(IllegalArgumentException.class, () -> tree.insert(null));
        assertThrows(IllegalArgumentException.class, () -> tree.contains(null));
        assertThrows(IllegalArgumentException.class, () -> tree.remove(null));
        assertThrows(NoSuchElementException.class, tree::min);
        assertThrows(NoSuchElementException.class, tree::max);
    }

    private BinarySearchTree<Integer> sampleTree() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<Integer>();
        tree.insert(8);
        tree.insert(3);
        tree.insert(10);
        tree.insert(1);
        tree.insert(6);
        tree.insert(14);
        tree.insert(4);
        tree.insert(7);
        tree.insert(13);
        return tree;
    }
}
