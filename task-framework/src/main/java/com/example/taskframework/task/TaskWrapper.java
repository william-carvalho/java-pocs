package com.example.taskframework.task;

import com.example.taskframework.model.TaskInfo;
import com.example.taskframework.model.TaskStatus;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class TaskWrapper<T> implements Callable<T> {

    private final Callable<T> delegate;
    private final TaskInfo taskInfo;

    public TaskWrapper(Callable<T> delegate, TaskInfo taskInfo) {
        this.delegate = delegate;
        this.taskInfo = taskInfo;
    }

    @Override
    public T call() throws Exception {
        taskInfo.setStatus(TaskStatus.RUNNING);
        taskInfo.setStartedAt(LocalDateTime.now());

        try {
            T result = delegate.call();
            taskInfo.setResult(result);
            taskInfo.setStatus(TaskStatus.COMPLETED);
            return result;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            taskInfo.setStatus(TaskStatus.CANCELLED);
            taskInfo.setErrorMessage("Task interrupted");
            throw ex;
        } catch (Exception ex) {
            taskInfo.setStatus(TaskStatus.FAILED);
            taskInfo.setErrorMessage(ex.getMessage());
            throw ex;
        } finally {
            taskInfo.setFinishedAt(LocalDateTime.now());
        }
    }
}

