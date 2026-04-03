package com.example.taxsystem.stresstest.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class StressTestRequest {

    @Valid
    @NotNull
    private StressTargetRequest target;

    @Valid
    @NotNull
    private StressExecutionRequest execution;

    public StressTargetRequest getTarget() {
        return target;
    }

    public void setTarget(StressTargetRequest target) {
        this.target = target;
    }

    public StressExecutionRequest getExecution() {
        return execution;
    }

    public void setExecution(StressExecutionRequest execution) {
        this.execution = execution;
    }
}
