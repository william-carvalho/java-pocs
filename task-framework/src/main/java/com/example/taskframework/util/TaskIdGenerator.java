package com.example.taskframework.util;

import java.util.concurrent.atomic.AtomicLong;

public class TaskIdGenerator {

    private final AtomicLong sequence = new AtomicLong(0);

    public String nextId() {
        return "task-" + sequence.incrementAndGet();
    }
}

