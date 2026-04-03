package com.example.taskframework.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface TaskExecutor {

    <T> Future<T> submit(Callable<T> callable);

    void shutdown();
}

