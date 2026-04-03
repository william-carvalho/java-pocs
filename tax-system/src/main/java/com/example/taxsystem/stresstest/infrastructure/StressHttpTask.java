package com.example.taxsystem.stresstest.infrastructure;

import com.example.taxsystem.stresstest.domain.StressTestConfig;

import java.util.concurrent.Callable;

public class StressHttpTask implements Callable<RequestExecutionResult> {

    private final StressHttpClient stressHttpClient;
    private final StressTestConfig config;

    public StressHttpTask(StressHttpClient stressHttpClient, StressTestConfig config) {
        this.stressHttpClient = stressHttpClient;
        this.config = config;
    }

    @Override
    public RequestExecutionResult call() {
        return stressHttpClient.execute(config);
    }
}
