package com.example.doublylinkedlist;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoublyLinkedListTest {
    @Test
    void addsValuesAtBeginningAndEnd() {
        DoublyLinkedList<String> list = new DoublyLinkedList<String>();

        list.addFirst("middle");
        list.addFirst("first");
        list.addLast("last");

        assertArrayEquals(new Object[]{"first", "middle", "last"}, list.toArray());
        assertArrayEquals(new Object[]{"last", "middle", "first"}, list.toReverseArray());
        assertEquals(3, list.size());
    }

    @Test
    void insertsValuesAtAnyValidPosition() {
        DoublyLinkedList<Integer> list = new DoublyLinkedList<Integer>();
        list.addLast(10);
        list.addLast(30);

        list.insertAt(1, 20);
        list.insertAt(0, 5);
        list.insertAt(4, 40);

        assertArrayEquals(new Object[]{5, 10, 20, 30, 40}, list.toArray());
        assertArrayEquals(new Object[]{40, 30, 20, 10, 5}, list.toReverseArray());
    }

    @Test
    void readsAndUpdatesValuesByIndex() {
        DoublyLinkedList<String> list = new DoublyLinkedList<String>();
        list.addLast("a");
        list.addLast("b");
        list.addLast("c");

        assertEquals("b", list.get(1));
        assertEquals("b", list.set(1, "B"));

        assertArrayEquals(new Object[]{"a", "B", "c"}, list.toArray());
    }

    @Test
    void removesFirstLastAndSpecificIndex() {
        DoublyLinkedList<Integer> list = new DoublyLinkedList<Integer>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        list.addLast(4);

        assertEquals(Integer.valueOf(1), list.removeFirst());
        assertEquals(Integer.valueOf(4), list.removeLast());
        assertEquals(Integer.valueOf(3), list.removeAt(1));

        assertArrayEquals(new Object[]{2}, list.toArray());
        assertArrayEquals(new Object[]{2}, list.toReverseArray());
    }

    @Test
    void removesFirstMatchingValue() {
        DoublyLinkedList<String> list = new DoublyLinkedList<String>();
        list.addLast("a");
        list.addLast("b");
        list.addLast("b");
        list.addLast("c");

        assertTrue(list.remove("b"));

        assertArrayEquals(new Object[]{"a", "b", "c"}, list.toArray());
        assertFalse(list.remove("missing"));
    }

    @Test
    void supportsNullValues() {
        DoublyLinkedList<String> list = new DoublyLinkedList<String>();
        list.addLast("a");
        list.addLast(null);
        list.addLast("b");

        assertTrue(list.contains(null));
        assertEquals(1, list.indexOf(null));
        assertTrue(list.remove(null));
        assertArrayEquals(new Object[]{"a", "b"}, list.toArray());
    }

    @Test
    void reversesListInPlace() {
        DoublyLinkedList<Integer> list = new DoublyLinkedList<Integer>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);

        list.reverse();

        assertArrayEquals(new Object[]{3, 2, 1}, list.toArray());
        assertArrayEquals(new Object[]{1, 2, 3}, list.toReverseArray());
        list.addLast(4);
        assertArrayEquals(new Object[]{3, 2, 1, 4}, list.toArray());
    }

    @Test
    void iteratesForwardAndBackward() {
        DoublyLinkedList<Integer> list = new DoublyLinkedList<Integer>();
        list.addLast(7);
        list.addLast(8);

        Iterator<Integer> forward = list.iterator();
        assertEquals(Integer.valueOf(7), forward.next());
        assertEquals(Integer.valueOf(8), forward.next());
        assertFalse(forward.hasNext());

        Iterator<Integer> backward = list.reverseIterator();
        assertEquals(Integer.valueOf(8), backward.next());
        assertEquals(Integer.valueOf(7), backward.next());
        assertFalse(backward.hasNext());
    }

    @Test
    void clearEmptiesList() {
        DoublyLinkedList<Integer> list = new DoublyLinkedList<Integer>();
        list.addLast(1);
        list.addLast(2);

        list.clear();

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertArrayEquals(new Object[0], list.toArray());
        assertArrayEquals(new Object[0], list.toReverseArray());
        list.addLast(3);
        assertArrayEquals(new Object[]{3}, list.toArray());
    }

    @Test
    void rejectsInvalidIndexesAndEmptyRemoval() {
        DoublyLinkedList<Integer> list = new DoublyLinkedList<Integer>();

        assertThrows(NoSuchElementException.class, list::removeFirst);
        assertThrows(NoSuchElementException.class, list::removeLast);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.insertAt(1, 10));

        list.addLast(1);

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(1, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeAt(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.insertAt(-1, 10));
    }

    @Test
    void iteratorsRejectInvalidOperations() {
        DoublyLinkedList<Integer> list = new DoublyLinkedList<Integer>();
        list.addLast(1);

        Iterator<Integer> forward = list.iterator();
        assertEquals(Integer.valueOf(1), forward.next());
        assertThrows(NoSuchElementException.class, forward::next);
        assertThrows(UnsupportedOperationException.class, forward::remove);

        Iterator<Integer> backward = list.reverseIterator();
        assertEquals(Integer.valueOf(1), backward.next());
        assertThrows(NoSuchElementException.class, backward::next);
        assertThrows(UnsupportedOperationException.class, backward::remove);
    }
}
