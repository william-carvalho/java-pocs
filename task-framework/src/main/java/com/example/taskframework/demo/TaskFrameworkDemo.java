package com.example.taskframework.demo;

import com.example.taskframework.core.DefaultTaskFramework;
import com.example.taskframework.core.TaskFramework;
import com.example.taskframework.executor.DefaultTaskExecutor;
import com.example.taskframework.model.TaskInfo;
import com.example.taskframework.model.TaskSubmissionResult;
import com.example.taskframework.model.TaskStatus;
import com.example.taskframework.registry.TaskRegistry;
import com.example.taskframework.util.TaskIdGenerator;
import java.util.Arrays;
import java.util.List;

public class TaskFrameworkDemo {

    public static void main(String[] args) throws Exception {
        TaskFramework framework = new DefaultTaskFramework(
                new DefaultTaskExecutor(4),
                new TaskRegistry(),
                new TaskIdGenerator()
        );

        TaskSubmissionResult task1 = framework.submit("print-task", new Runnable() {
            @Override
            public void run() {
                System.out.println("Executing runnable task");
            }
        });

        TaskSubmissionResult task2 = framework.submit("sum-task", () -> 10 + 20);

        TaskSubmissionResult task3 = framework.submit("sleep-task", () -> {
            Thread.sleep(2000);
            return "Long task finished";
        });

        TaskSubmissionResult task4 = framework.submit("failure-task", () -> {
            throw new IllegalStateException("Simulated failure");
        });

        System.out.println("Submitted task: " + task1.getTaskId());
        System.out.println("Submitted task: " + task2.getTaskId());
        System.out.println("Submitted task: " + task3.getTaskId());
        System.out.println("Submitted task: " + task4.getTaskId());
        System.out.println();

        for (int i = 0; i < 5; i++) {
            Thread.sleep(700);
            printStatuses(framework.listTasks());
            System.out.println();
        }

        System.out.println("Task " + task2.getTaskId() + " result: " + framework.getResult(task2.getTaskId()));
        System.out.println("Task " + task3.getTaskId() + " result: " + framework.getResult(task3.getTaskId()));
        System.out.println("Task " + task4.getTaskId() + " error: " + framework.getTask(task4.getTaskId()).getErrorMessage());

        framework.shutdown();
        System.out.println("Framework shutdown completed.");
    }

    private static void printStatuses(List<TaskInfo> taskInfos) {
        for (TaskInfo taskInfo : taskInfos) {
            System.out.println("Task " + taskInfo.getTaskId() + " status: " + taskInfo.getStatus());
        }
    }
}
