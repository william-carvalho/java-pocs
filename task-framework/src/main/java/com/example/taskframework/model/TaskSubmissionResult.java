package com.example.taskframework.model;

public class TaskSubmissionResult {

    private final String taskId;
    private final TaskStatus status;

    public TaskSubmissionResult(String taskId, TaskStatus status) {
        this.taskId = taskId;
        this.status = status;
    }

    public String getTaskId() {
        return taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }
}

