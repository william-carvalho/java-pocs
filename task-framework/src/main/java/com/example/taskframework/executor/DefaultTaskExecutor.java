package com.example.taskframework.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DefaultTaskExecutor implements TaskExecutor {

    private final ExecutorService executorService;

    public DefaultTaskExecutor(int poolSize) {
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        return executorService.submit(callable);
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            executorService.shutdownNow();
        }
    }
}

