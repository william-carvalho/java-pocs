package com.example.taskframework.registry;

import com.example.taskframework.exception.TaskNotFoundException;
import com.example.taskframework.model.TaskInfo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class TaskRegistry {

    private final Map<String, TaskInfo> tasks = new ConcurrentHashMap<String, TaskInfo>();
    private final Map<String, Future<?>> futures = new ConcurrentHashMap<String, Future<?>>();

    public void register(TaskInfo taskInfo, Future<?> future) {
        tasks.put(taskInfo.getTaskId(), taskInfo);
        futures.put(taskInfo.getTaskId(), future);
    }

    public TaskInfo getTask(String taskId) {
        TaskInfo taskInfo = tasks.get(taskId);
        if (taskInfo == null) {
            throw new TaskNotFoundException("Task not found with id " + taskId);
        }
        return taskInfo;
    }

    public Future<?> getFuture(String taskId) {
        Future<?> future = futures.get(taskId);
        if (future == null) {
            throw new TaskNotFoundException("Task not found with id " + taskId);
        }
        return future;
    }

    public List<TaskInfo> listTasks() {
        List<TaskInfo> result = new ArrayList<TaskInfo>(tasks.values());
        result.sort(Comparator.comparing(TaskInfo::getCreatedAt));
        return result;
    }
}

