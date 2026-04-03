package com.example.taskframework;

import com.example.taskframework.core.DefaultTaskFramework;
import com.example.taskframework.core.TaskFramework;
import com.example.taskframework.exception.FrameworkShutdownException;
import com.example.taskframework.exception.TaskNotFoundException;
import com.example.taskframework.executor.DefaultTaskExecutor;
import com.example.taskframework.model.TaskInfo;
import com.example.taskframework.model.TaskStatus;
import com.example.taskframework.registry.TaskRegistry;
import com.example.taskframework.util.TaskIdGenerator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultTaskFrameworkTest {

    private TaskFramework framework;

    @BeforeEach
    void setUp() {
        framework = new DefaultTaskFramework(
                new DefaultTaskExecutor(2),
                new TaskRegistry(),
                new TaskIdGenerator()
        );
    }

    @AfterEach
    void tearDown() {
        framework.shutdown();
    }

    @Test
    void shouldExecuteRunnableAndUpdateStatus() throws Exception {
        String taskId = framework.submit("print", new Runnable() {
            @Override
            public void run() {
            }
        }).getTaskId();

        waitUntilFinished(taskId);

        TaskInfo taskInfo = framework.getTask(taskId);
        assertEquals(TaskStatus.COMPLETED, taskInfo.getStatus());
        assertEquals("Runnable task completed", framework.getResult(taskId));
    }

    @Test
    void shouldExecuteCallableAndStoreResult() throws Exception {
        String taskId = framework.submit("sum", () -> 10 + 20).getTaskId();

        waitUntilFinished(taskId);

        assertEquals(TaskStatus.COMPLETED, framework.getTask(taskId).getStatus());
        assertEquals(30, framework.getResult(taskId));
    }

    @Test
    void shouldMarkTaskAsFailedWhenExceptionHappens() throws Exception {
        String taskId = framework.submit("failure", () -> {
            throw new IllegalStateException("Simulated failure");
        }).getTaskId();

        waitUntilFinished(taskId);

        TaskInfo taskInfo = framework.getTask(taskId);
        assertEquals(TaskStatus.FAILED, taskInfo.getStatus());
        assertEquals("Simulated failure", taskInfo.getErrorMessage());
    }

    @Test
    void shouldListSubmittedTasks() {
        framework.submit("task-1", () -> 1);
        framework.submit("task-2", () -> 2);

        List<TaskInfo> tasks = framework.listTasks();

        assertEquals(2, tasks.size());
    }

    @Test
    void shouldThrowWhenTaskDoesNotExist() {
        assertThrows(TaskNotFoundException.class, () -> framework.getTask("missing-task"));
    }

    @Test
    void shouldCancelLongRunningTask() throws Exception {
        String taskId = framework.submit("sleep", () -> {
            Thread.sleep(5000);
            return "done";
        }).getTaskId();

        boolean cancelled = framework.cancel(taskId);

        assertTrue(cancelled);
        Thread.sleep(200);
        assertEquals(TaskStatus.CANCELLED, framework.getTask(taskId).getStatus());
    }

    @Test
    void shouldRejectSubmitAfterShutdown() {
        framework.shutdown();
        assertThrows(FrameworkShutdownException.class, () -> framework.submit(() -> 1));
    }

    private void waitUntilFinished(String taskId) throws Exception {
        for (int i = 0; i < 20; i++) {
            TaskStatus status = framework.getTask(taskId).getStatus();
            if (status == TaskStatus.COMPLETED || status == TaskStatus.FAILED || status == TaskStatus.CANCELLED) {
                return;
            }
            Thread.sleep(100);
        }
    }
}

