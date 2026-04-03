package com.example.httpstresstest.demo;

import com.example.httpstresstest.config.HttpMethod;
import com.example.httpstresstest.config.StressTestConfig;
import com.example.httpstresstest.core.StressTestFramework;
import com.example.httpstresstest.report.ConsoleReportPrinter;
import com.example.httpstresstest.report.StressTestReport;

public class HttpStressTestApplication {

    public static void main(String[] args) throws Exception {
        DemoHttpServer demoHttpServer = null;

        try {
            StressTestConfig successConfig;
            StressTestConfig failureConfig;

            if (args != null && args.length >= 4) {
                successConfig = StressTestConfig.builder()
                        .url(args[0])
                        .method(HttpMethod.valueOf(args[1].toUpperCase()))
                        .totalRequests(Integer.parseInt(args[2]))
                        .threadPoolSize(Integer.parseInt(args[3]))
                        .connectTimeoutMs(3000)
                        .readTimeoutMs(3000)
                        .build();

                failureConfig = StressTestConfig.builder()
                        .url(args[0] + "/invalid")
                        .method(HttpMethod.GET)
                        .totalRequests(20)
                        .threadPoolSize(Math.min(5, Integer.parseInt(args[3])))
                        .connectTimeoutMs(1000)
                        .readTimeoutMs(1000)
                        .build();
            } else {
                int demoPort = 9091;
                demoHttpServer = new DemoHttpServer(demoPort);
                demoHttpServer.start();

                successConfig = StressTestConfig.builder()
                        .url("http://localhost:" + demoPort + "/health")
                        .method(HttpMethod.GET)
                        .totalRequests(100)
                        .threadPoolSize(10)
                        .connectTimeoutMs(2000)
                        .readTimeoutMs(2000)
                        .build();

                failureConfig = StressTestConfig.builder()
                        .url("http://localhost:" + demoPort + "/unknown")
                        .method(HttpMethod.GET)
                        .totalRequests(30)
                        .threadPoolSize(5)
                        .connectTimeoutMs(2000)
                        .readTimeoutMs(2000)
                        .build();
            }

            StressTestFramework framework = new StressTestFramework();

            System.out.println("Running success scenario...");
            StressTestReport successReport = framework.run(successConfig);
            ConsoleReportPrinter.print(successReport);
            System.out.println();

            System.out.println("Running failure scenario...");
            StressTestReport failureReport = framework.run(failureConfig);
            ConsoleReportPrinter.print(failureReport);
        } finally {
            if (demoHttpServer != null) {
                demoHttpServer.stop();
            }
        }
    }
}

