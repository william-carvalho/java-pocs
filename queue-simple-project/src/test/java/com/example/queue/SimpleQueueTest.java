package com.example.queue;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleQueueTest {
    @Test
    void removesValuesInFirstInFirstOutOrder() {
        SimpleQueue<String> queue = new SimpleQueue<String>();
        queue.enqueue("first");
        queue.enqueue("second");

        assertEquals("first", queue.dequeue());
        assertEquals("second", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void peekDoesNotRemoveTheFrontValue() {
        SimpleQueue<Integer> queue = new SimpleQueue<Integer>();
        queue.enqueue(10);

        assertEquals(Integer.valueOf(10), queue.peek());
        assertEquals(1, queue.size());
    }

    @Test
    void canBeReusedAfterBecomingEmpty() {
        SimpleQueue<String> queue = new SimpleQueue<String>();
        queue.enqueue("old");
        queue.dequeue();
        queue.enqueue("new");

        assertEquals("new", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void emptyQueueCannotBeDequeued() {
        SimpleQueue<Integer> queue = new SimpleQueue<Integer>();

        assertThrows(NoSuchElementException.class, queue::dequeue);
    }
}
