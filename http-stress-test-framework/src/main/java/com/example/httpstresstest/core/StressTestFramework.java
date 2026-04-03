package com.example.httpstresstest.core;

import com.example.httpstresstest.config.StressTestConfig;
import com.example.httpstresstest.executor.StressTestRunner;
import com.example.httpstresstest.report.StressTestReport;

public class StressTestFramework {

    private final StressTestRunner stressTestRunner;

    public StressTestFramework() {
        this.stressTestRunner = new StressTestRunner();
    }

    public StressTestReport run(StressTestConfig config) {
        return stressTestRunner.run(config);
    }
}

