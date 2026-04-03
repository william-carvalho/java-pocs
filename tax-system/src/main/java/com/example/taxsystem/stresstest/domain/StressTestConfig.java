package com.example.taxsystem.stresstest.domain;

import com.example.taxsystem.common.annotation.Mappable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Mappable
public class StressTestConfig {

    @Valid
    @NotNull
    private StressTargetDefinition target;

    @Valid
    @NotNull
    private StressExecutionDefinition execution;

    public StressTargetDefinition getTarget() {
        return target;
    }

    public void setTarget(StressTargetDefinition target) {
        this.target = target;
    }

    public StressExecutionDefinition getExecution() {
        return execution;
    }

    public void setExecution(StressExecutionDefinition execution) {
        this.execution = execution;
    }
}
