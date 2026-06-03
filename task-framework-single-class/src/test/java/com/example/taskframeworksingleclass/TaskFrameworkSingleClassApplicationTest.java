package com.example.taskframeworksingleclass;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskFrameworkSingleClassApplicationTest {

    @Test
    void submitsTaskToOwnThreadPool() throws Exception {
        TaskFrameworkSingleClassApplication.TaskFramework framework =
                new TaskFrameworkSingleClassApplication.TaskFramework(1);

        TaskFrameworkSingleClassApplication.TaskInfo task = framework.submit("send-email", 10);

        waitUntilDone(task);

        assertThat(task.status).isEqualTo("DONE");
        assertThat(task.workerName).startsWith("task-worker-");
        assertThat(task.startedAt).isNotNull();
        assertThat(task.finishedAt).isNotNull();

        framework.shutdown();
    }

    @Test
    void runsTasksInParallelAcrossPoolWorkers() throws Exception {
        TaskFrameworkSingleClassApplication.TaskFramework framework =
                new TaskFrameworkSingleClassApplication.TaskFramework(2);

        TaskFrameworkSingleClassApplication.TaskInfo first = framework.submit("first", 80);
        TaskFrameworkSingleClassApplication.TaskInfo second = framework.submit("second", 80);

        waitUntilDone(first);
        waitUntilDone(second);

        Set<String> workers = new HashSet<String>();
        workers.add(first.workerName);
        workers.add(second.workerName);

        assertThat(workers).hasSize(2);

        framework.shutdown();
    }

    @Test
    void listsSubmittedTasks() throws Exception {
        TaskFrameworkSingleClassApplication.TaskFramework framework =
                new TaskFrameworkSingleClassApplication.TaskFramework(1);

        TaskFrameworkSingleClassApplication.TaskInfo first = framework.submit("one", 1);
        TaskFrameworkSingleClassApplication.TaskInfo second = framework.submit("two", 1);

        waitUntilDone(first);
        waitUntilDone(second);

        assertThat(framework.list()).hasSize(2);
        assertThat(framework.get(first.id).name).isEqualTo("one");

        framework.shutdown();
    }

    @Test
    void canGrowPoolWithResize() {
        TaskFrameworkSingleClassApplication.TaskFramework framework =
                new TaskFrameworkSingleClassApplication.TaskFramework(1);

        framework.resize(3);

        assertThat(framework.workerCount()).isEqualTo(3);

        framework.shutdown();
    }

    @Test
    void rejectsInvalidPoolSize() {
        assertThatThrownBy(() -> new TaskFrameworkSingleClassApplication.TaskFramework(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("workerCount");
    }

    private void waitUntilDone(TaskFrameworkSingleClassApplication.TaskInfo task) throws Exception {
        long deadline = System.currentTimeMillis() + 2000;
        while (!"DONE".equals(task.status) && System.currentTimeMillis() < deadline) {
            Thread.sleep(10);
        }
        assertThat(task.status).isEqualTo("DONE");
    }
}
