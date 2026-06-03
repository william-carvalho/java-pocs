package com.example.taskframeworksingleclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
@RestController
public class TaskFrameworkSingleClassApplication {

    private final TaskFramework framework = new TaskFramework(3);

    public static void main(String[] args) {
        SpringApplication.run(TaskFrameworkSingleClassApplication.class, args);
    }

    @PostMapping("/tasks")
    public TaskInfo submit(@RequestBody TaskRequest request) {
        if (request == null || request.name == null || request.name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "task name is required");
        }
        return framework.submit(request.name, request.durationMillis);
    }

    @GetMapping("/tasks/{id}")
    public TaskInfo get(@PathVariable String id) {
        TaskInfo task = framework.get(id);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "task not found: " + id);
        }
        return task;
    }

    @GetMapping("/tasks")
    public List<TaskInfo> list() {
        return framework.list();
    }

    @PostMapping("/pool/resize")
    public Map<String, Object> resize(@RequestParam int size) {
        framework.resize(size);

        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put("workers", framework.workerCount());
        return response;
    }

    @PreDestroy
    public void shutdown() {
        framework.shutdown();
    }

    public TaskFramework getFramework() {
        return framework;
    }

    public static class TaskRequest {
        public String name;
        public long durationMillis;
    }

    public static class TaskFramework {
        private final Map<String, TaskInfo> tasks = new ConcurrentHashMap<String, TaskInfo>();
        private final BlockingQueue<TaskInfo> queue = new LinkedBlockingQueue<TaskInfo>();
        private final List<Thread> workers = new ArrayList<Thread>();
        private final AtomicBoolean running = new AtomicBoolean(true);

        public TaskFramework(int workerCount) {
            resize(workerCount);
        }

        public TaskInfo submit(String name, long durationMillis) {
            if (!running.get()) {
                throw new IllegalStateException("framework is stopped");
            }

            TaskInfo task = new TaskInfo(UUID.randomUUID().toString(), name, Math.max(0, durationMillis));
            tasks.put(task.id, task);
            queue.offer(task);
            return task;
        }

        public TaskInfo get(String id) {
            return tasks.get(id);
        }

        public List<TaskInfo> list() {
            return new ArrayList<TaskInfo>(tasks.values());
        }

        public synchronized void resize(int workerCount) {
            if (workerCount <= 0) {
                throw new IllegalArgumentException("workerCount must be greater than zero");
            }
            while (workers.size() < workerCount) {
                Thread worker = new Thread(new Worker(), "task-worker-" + (workers.size() + 1));
                worker.setDaemon(true);
                workers.add(worker);
                worker.start();
            }
        }

        public int workerCount() {
            return workers.size();
        }

        public void shutdown() {
            running.set(false);
            for (Thread worker : workers) {
                worker.interrupt();
            }
        }

        private class Worker implements Runnable {
            @Override
            public void run() {
                while (running.get()) {
                    try {
                        TaskInfo task = queue.poll(100, TimeUnit.MILLISECONDS);
                        if (task != null) {
                            runTask(task);
                        }
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            private void runTask(TaskInfo task) throws InterruptedException {
                task.status = "RUNNING";
                task.startedAt = LocalDateTime.now();
                task.workerName = Thread.currentThread().getName();

                Thread.sleep(task.durationMillis);

                task.status = "DONE";
                task.finishedAt = LocalDateTime.now();
            }
        }
    }

    public static class TaskInfo {
        public String id;
        public String name;
        public String status;
        public long durationMillis;
        public String workerName;
        public LocalDateTime createdAt;
        public LocalDateTime startedAt;
        public LocalDateTime finishedAt;

        public TaskInfo() {
        }

        public TaskInfo(String id, String name, long durationMillis) {
            this.id = id;
            this.name = name;
            this.durationMillis = durationMillis;
            this.status = "QUEUED";
            this.createdAt = LocalDateTime.now();
        }
    }
}
