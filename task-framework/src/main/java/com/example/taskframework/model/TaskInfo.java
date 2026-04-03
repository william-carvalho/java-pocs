package com.example.taskframework.model;

import java.time.LocalDateTime;

public class TaskInfo {

    private final String taskId;
    private final String taskName;
    private final LocalDateTime createdAt;
    private volatile TaskStatus status;
    private volatile LocalDateTime startedAt;
    private volatile LocalDateTime finishedAt;
    private volatile Object result;
    private volatile String errorMessage;

    public TaskInfo(String taskId, String taskName, TaskStatus status, LocalDateTime createdAt) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "taskId='" + taskId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", startedAt=" + startedAt +
                ", finishedAt=" + finishedAt +
                ", result=" + result +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}

