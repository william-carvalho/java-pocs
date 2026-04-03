package com.example.taskframework.core;

import com.example.taskframework.exception.FrameworkShutdownException;
import com.example.taskframework.model.TaskInfo;
import com.example.taskframework.model.TaskStatus;
import com.example.taskframework.model.TaskSubmissionResult;
import com.example.taskframework.executor.TaskExecutor;
import com.example.taskframework.registry.TaskRegistry;
import com.example.taskframework.task.TaskWrapper;
import com.example.taskframework.util.TaskIdGenerator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultTaskFramework implements TaskFramework {

    private final TaskExecutor taskExecutor;
    private final TaskRegistry taskRegistry;
    private final TaskIdGenerator taskIdGenerator;
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    public DefaultTaskFramework(TaskExecutor taskExecutor, TaskRegistry taskRegistry, TaskIdGenerator taskIdGenerator) {
        this.taskExecutor = taskExecutor;
        this.taskRegistry = taskRegistry;
        this.taskIdGenerator = taskIdGenerator;
    }

    @Override
    public TaskSubmissionResult submit(Runnable task) {
        return submit("runnable-task", runnableToCallable(task));
    }

    @Override
    public TaskSubmissionResult submit(String taskName, Runnable task) {
        return submit(taskName, runnableToCallable(task));
    }

    @Override
    public <T> TaskSubmissionResult submit(Callable<T> task) {
        return submit("callable-task", task);
    }

    @Override
    public <T> TaskSubmissionResult submit(String taskName, Callable<T> task) {
        ensureActive();

        String taskId = taskIdGenerator.nextId();
        TaskInfo taskInfo = new TaskInfo(taskId, taskName, TaskStatus.PENDING, LocalDateTime.now());
        Future<T> future = taskExecutor.submit(new TaskWrapper<T>(task, taskInfo));
        taskRegistry.register(taskInfo, future);
        return new TaskSubmissionResult(taskId, TaskStatus.PENDING);
    }

    @Override
    public TaskInfo getTask(String taskId) {
        return taskRegistry.getTask(taskId);
    }

    @Override
    public Object getResult(String taskId) {
        return taskRegistry.getTask(taskId).getResult();
    }

    @Override
    public List<TaskInfo> listTasks() {
        return taskRegistry.listTasks();
    }

    @Override
    public boolean cancel(String taskId) {
        TaskInfo taskInfo = taskRegistry.getTask(taskId);
        boolean cancelled = taskRegistry.getFuture(taskId).cancel(true);
        if (cancelled && taskInfo.getStatus() != TaskStatus.COMPLETED && taskInfo.getStatus() != TaskStatus.FAILED) {
            taskInfo.setStatus(TaskStatus.CANCELLED);
            taskInfo.setFinishedAt(LocalDateTime.now());
        }
        return cancelled;
    }

    @Override
    public void shutdown() {
        if (shutdown.compareAndSet(false, true)) {
            taskExecutor.shutdown();
        }
    }

    private void ensureActive() {
        if (shutdown.get()) {
            throw new FrameworkShutdownException("Task framework is already shutdown");
        }
    }

    private Callable<Object> runnableToCallable(final Runnable task) {
        return new Callable<Object>() {
            @Override
            public Object call() {
                task.run();
                return "Runnable task completed";
            }
        };
    }
}

