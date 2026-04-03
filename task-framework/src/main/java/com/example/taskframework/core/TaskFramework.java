package com.example.taskframework.core;

import com.example.taskframework.exception.FrameworkShutdownException;
import com.example.taskframework.model.TaskInfo;
import com.example.taskframework.model.TaskSubmissionResult;
import java.util.List;
import java.util.concurrent.Callable;

public interface TaskFramework {

    TaskSubmissionResult submit(Runnable task);

    TaskSubmissionResult submit(String taskName, Runnable task);

    <T> TaskSubmissionResult submit(Callable<T> task);

    <T> TaskSubmissionResult submit(String taskName, Callable<T> task);

    TaskInfo getTask(String taskId);

    Object getResult(String taskId);

    List<TaskInfo> listTasks();

    boolean cancel(String taskId);

    void shutdown();
}
