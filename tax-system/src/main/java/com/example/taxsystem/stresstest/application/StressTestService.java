package com.example.taxsystem.stresstest.application;

import com.example.taxsystem.common.config.ShowcaseProperties;
import com.example.taxsystem.common.config.StressExecutionContext;
import com.example.taxsystem.common.validation.ValidationSupport;
import com.example.taxsystem.converter.application.ConverterEngine;
import com.example.taxsystem.stresstest.api.StressAsyncResponse;
import com.example.taxsystem.stresstest.api.StressTestRequest;
import com.example.taxsystem.stresstest.domain.StressExecutionStatus;
import com.example.taxsystem.stresstest.domain.StressExecutionMode;
import com.example.taxsystem.stresstest.domain.StressJobStatus;
import com.example.taxsystem.stresstest.domain.StressTestConfig;
import com.example.taxsystem.stresstest.domain.StressTestReport;
import com.example.taxsystem.stresstest.infrastructure.HttpStressTestRunner;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Service
public class StressTestService {

    private final ConverterEngine converterEngine;
    private final ValidationSupport validationSupport;
    private final HttpStressTestRunner httpStressTestRunner;
    private final ObjectProvider<StressExecutionContext> contextProvider;
    private final Executor stressAsyncExecutor;
    private final ShowcaseProperties showcaseProperties;
    private final Map<String, StressExecutionStatus> executionStore = new ConcurrentHashMap<String, StressExecutionStatus>();

    public StressTestService(ConverterEngine converterEngine,
                             ValidationSupport validationSupport,
                             HttpStressTestRunner httpStressTestRunner,
                             ObjectProvider<StressExecutionContext> contextProvider,
                             @Qualifier("stressAsyncExecutor") Executor stressAsyncExecutor,
                             ShowcaseProperties showcaseProperties) {
        this.converterEngine = converterEngine;
        this.validationSupport = validationSupport;
        this.httpStressTestRunner = httpStressTestRunner;
        this.contextProvider = contextProvider;
        this.stressAsyncExecutor = stressAsyncExecutor;
        this.showcaseProperties = showcaseProperties;
    }

    public StressTestReport runSync(StressTestRequest request) {
        StressExecutionContext context = contextProvider.getObject();
        StressTestConfig config = toConfig(request);
        config.getExecution().setMode(StressExecutionMode.SYNC);
        return httpStressTestRunner.run(config, context);
    }

    public StressAsyncResponse runAsync(StressTestRequest request) {
        StressExecutionContext context = contextProvider.getObject();
        StressTestConfig config = toConfig(request);
        config.getExecution().setMode(StressExecutionMode.ASYNC);
        executionStore.put(context.getExecutionId(), StressExecutionStatus.running(context.getExecutionId(), context.getCreatedAt()));

        Supplier<StressTestReport> supplier = new Supplier<StressTestReport>() {
            @Override
            public StressTestReport get() {
                return httpStressTestRunner.run(config, context);
            }
        };

        CompletableFuture.supplyAsync(supplier, stressAsyncExecutor)
                .whenComplete((report, throwable) -> {
                    if (throwable != null) {
                        executionStore.put(context.getExecutionId(),
                                StressExecutionStatus.failed(context.getExecutionId(), context.getCreatedAt(), throwable.getMessage()));
                        return;
                    }
                    executionStore.put(context.getExecutionId(), StressExecutionStatus.completed(report));
                });

        return new StressAsyncResponse(context.getExecutionId(), StressJobStatus.RUNNING, context.getCreatedAt());
    }

    public StressExecutionStatus getStatus(String executionId) {
        StressExecutionStatus status = executionStore.get(executionId);
        if (status == null) {
            throw new EntityNotFoundException("Stress execution not found: " + executionId);
        }
        return status;
    }

    public StressTestConfig previewConfig(Object request) {
        return toConfig(request);
    }

    private StressTestConfig toConfig(Object request) {
        StressTestConfig config = converterEngine.convert(request, StressTestConfig.class);
        validationSupport.validate(config);
        if (config.getExecution().getConcurrencyLevel() > showcaseProperties.getStress().getMaxConcurrency()) {
            throw new IllegalArgumentException("Concurrency exceeds configured maximum of " + showcaseProperties.getStress().getMaxConcurrency());
        }
        return config;
    }
}
