package com.example.circularlinkedlist;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CircularLinkedListTest {
    @Test
    void addsValuesAtBeginningAndEnd() {
        CircularLinkedList<String> list = new CircularLinkedList<String>();

        list.addFirst("middle");
        list.addFirst("first");
        list.addLast("last");

        assertArrayEquals(new Object[]{"first", "middle", "last"}, list.toArray());
        assertEquals(3, list.size());
        assertTrue(list.isCircular());
    }

    @Test
    void insertsValuesAtAnyValidPosition() {
        CircularLinkedList<Integer> list = new CircularLinkedList<Integer>();
        list.addLast(10);
        list.addLast(30);

        list.insertAt(1, 20);
        list.insertAt(0, 5);
        list.insertAt(4, 40);

        assertArrayEquals(new Object[]{5, 10, 20, 30, 40}, list.toArray());
        assertTrue(list.isCircular());
    }

    @Test
    void readsValuesByIndex() {
        CircularLinkedList<String> list = new CircularLinkedList<String>();
        list.addLast("a");
        list.addLast("b");
        list.addLast("c");

        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    void removesFirstLastAndSpecificIndex() {
        CircularLinkedList<Integer> list = new CircularLinkedList<Integer>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        list.addLast(4);

        assertEquals(Integer.valueOf(1), list.removeFirst());
        assertEquals(Integer.valueOf(4), list.removeLast());
        assertEquals(Integer.valueOf(3), list.removeAt(1));

        assertArrayEquals(new Object[]{2}, list.toArray());
        assertTrue(list.isCircular());
    }

    @Test
    void removesFirstMatchingValueAndMaintainsCircle() {
        CircularLinkedList<String> list = new CircularLinkedList<String>();
        list.addLast("a");
        list.addLast("b");
        list.addLast("b");
        list.addLast("c");

        assertTrue(list.remove("b"));
        assertTrue(list.remove("c"));
        list.addLast("d");

        assertArrayEquals(new Object[]{"a", "b", "d"}, list.toArray());
        assertTrue(list.isCircular());
        assertFalse(list.remove("missing"));
    }

    @Test
    void supportsNullValues() {
        CircularLinkedList<String> list = new CircularLinkedList<String>();
        list.addLast("a");
        list.addLast(null);
        list.addLast("b");

        assertTrue(list.contains(null));
        assertEquals(1, list.indexOf(null));
        assertTrue(list.remove(null));
        assertArrayEquals(new Object[]{"a", "b"}, list.toArray());
    }

    @Test
    void rotatesHeadForwardAndBackward() {
        CircularLinkedList<Integer> list = new CircularLinkedList<Integer>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        list.addLast(4);

        list.rotate(1);
        assertArrayEquals(new Object[]{2, 3, 4, 1}, list.toArray());

        list.rotate(-2);
        assertArrayEquals(new Object[]{4, 1, 2, 3}, list.toArray());

        list.rotate(8);
        assertArrayEquals(new Object[]{4, 1, 2, 3}, list.toArray());
        assertTrue(list.isCircular());
    }

    @Test
    void iteratorStopsAfterOneCycle() {
        CircularLinkedList<Integer> list = new CircularLinkedList<Integer>();
        list.addLast(7);
        list.addLast(8);

        Iterator<Integer> iterator = list.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(7), iterator.next());
        assertEquals(Integer.valueOf(8), iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void clearEmptiesList() {
        CircularLinkedList<Integer> list = new CircularLinkedList<Integer>();
        list.addLast(1);
        list.addLast(2);

        list.clear();

        assertTrue(list.isEmpty());
        assertTrue(list.isCircular());
        assertEquals(0, list.size());
        assertArrayEquals(new Object[0], list.toArray());
        list.addLast(3);
        assertArrayEquals(new Object[]{3}, list.toArray());
        assertTrue(list.isCircular());
    }

    @Test
    void rejectsInvalidIndexesAndEmptyRemoval() {
        CircularLinkedList<Integer> list = new CircularLinkedList<Integer>();

        assertThrows(NoSuchElementException.class, list::removeFirst);
        assertThrows(NoSuchElementException.class, list::removeLast);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.insertAt(1, 10));

        list.addLast(1);

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeAt(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.insertAt(-1, 10));
    }
}
