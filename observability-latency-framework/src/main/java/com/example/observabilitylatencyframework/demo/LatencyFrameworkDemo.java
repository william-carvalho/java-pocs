package com.example.observabilitylatencyframework.demo;

import com.example.observabilitylatencyframework.core.DefaultLatencyFramework;
import com.example.observabilitylatencyframework.core.LatencyFramework;
import com.example.observabilitylatencyframework.metric.LatencyMetric;
import com.example.observabilitylatencyframework.util.MetricPrinter;

public class LatencyFrameworkDemo {

    public static void main(String[] args) {
        LatencyFramework framework = new DefaultLatencyFramework();

        framework.record("db.query.users", 120);
        framework.record("db.query.users", 80);

        String user = framework.measure("user-service.findUser", () -> {
            sleep(100);
            return "William";
        });

        framework.measure("payment.process", () -> sleep(200));
        framework.measure("report.generate", () -> sleep(60));

        try {
            framework.measure("external.api.call", () -> {
                sleep(50);
                throw new RuntimeException("timeout");
            });
        } catch (RuntimeException exception) {
            System.out.println("Captured expected error: " + exception.getMessage());
        }

        System.out.println("Measured user: " + user);
        System.out.println();

        LatencyMetric userMetric = framework.getMetric("user-service.findUser");
        MetricPrinter.printMetric(userMetric);
        System.out.println();
        MetricPrinter.printAll(framework.getAllMetrics());

        framework.reset("payment.process");
        System.out.println("After reset(payment.process): " + framework.getAllMetrics().size() + " metrics");

        framework.resetAll();
        System.out.println("After resetAll(): " + framework.getAllMetrics().size() + " metrics");
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted during demo", exception);
        }
    }
}

