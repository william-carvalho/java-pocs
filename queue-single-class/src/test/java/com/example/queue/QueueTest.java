package com.example.queue;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueueTest {
    @Test
    void startsEmpty() {
        Queue<String> queue = new Queue<String>();

        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertArrayEquals(new Object[0], queue.toArray());
    }

    @Test
    void enqueuesValuesAtBack() {
        Queue<String> queue = new Queue<String>();

        queue.enqueue("a");
        queue.enqueue("b");
        queue.enqueue("c");

        assertArrayEquals(new Object[]{"a", "b", "c"}, queue.toArray());
        assertEquals(3, queue.size());
        assertEquals("a", queue.front());
        assertEquals("c", queue.back());
    }

    @Test
    void dequeuesValuesInFirstInFirstOutOrder() {
        Queue<Integer> queue = new Queue<Integer>();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);

        assertEquals(Integer.valueOf(1), queue.dequeue());
        assertEquals(Integer.valueOf(2), queue.dequeue());
        assertEquals(Integer.valueOf(3), queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void peekReturnsFrontWithoutRemovingIt() {
        Queue<Integer> queue = new Queue<Integer>();
        queue.enqueue(10);
        queue.enqueue(20);

        assertEquals(Integer.valueOf(10), queue.peek());
        assertEquals(Integer.valueOf(10), queue.front());
        assertEquals(2, queue.size());
        assertEquals(Integer.valueOf(10), queue.dequeue());
    }

    @Test
    void containsFindsStoredValues() {
        Queue<String> queue = new Queue<String>();
        queue.enqueue("a");
        queue.enqueue("b");

        assertTrue(queue.contains("a"));
        assertTrue(queue.contains("b"));
        assertFalse(queue.contains("c"));
    }

    @Test
    void supportsNullValues() {
        Queue<String> queue = new Queue<String>();
        queue.enqueue(null);
        queue.enqueue("b");

        assertTrue(queue.contains(null));
        assertArrayEquals(new Object[]{null, "b"}, queue.toArray());
        assertEquals(null, queue.peek());
        assertEquals(null, queue.dequeue());
        assertEquals("b", queue.dequeue());
    }

    @Test
    void clearResetsQueueForReuse() {
        Queue<Integer> queue = new Queue<Integer>();
        queue.enqueue(1);
        queue.enqueue(2);

        queue.clear();

        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertArrayEquals(new Object[0], queue.toArray());

        queue.enqueue(3);
        queue.enqueue(4);
        assertArrayEquals(new Object[]{3, 4}, queue.toArray());
    }

    @Test
    void iteratorVisitsInQueueOrder() {
        Queue<Integer> queue = new Queue<Integer>();
        queue.enqueue(7);
        queue.enqueue(8);

        Iterator<Integer> iterator = queue.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(7), iterator.next());
        assertEquals(Integer.valueOf(8), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void rejectsEmptyReadsAndRemovals() {
        Queue<Integer> queue = new Queue<Integer>();

        assertThrows(NoSuchElementException.class, queue::dequeue);
        assertThrows(NoSuchElementException.class, queue::peek);
        assertThrows(NoSuchElementException.class, queue::front);
        assertThrows(NoSuchElementException.class, queue::back);
    }

    @Test
    void iteratorRejectsInvalidOperations() {
        Queue<Integer> queue = new Queue<Integer>();
        queue.enqueue(1);

        Iterator<Integer> iterator = queue.iterator();

        assertEquals(Integer.valueOf(1), iterator.next());
        assertThrows(NoSuchElementException.class, iterator::next);
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }
}
