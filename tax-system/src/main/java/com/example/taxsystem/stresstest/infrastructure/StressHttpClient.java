package com.example.taxsystem.stresstest.infrastructure;

import com.example.taxsystem.stresstest.domain.StressTargetDefinition;
import com.example.taxsystem.stresstest.domain.StressTestConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.Callable;

@Component
public class StressHttpClient {

    private final RestTemplateBuilder restTemplateBuilder;
    private final Retry retry;

    public StressHttpClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.retry = Retry.of("stressHttpClient", RetryConfig.custom()
                .maxAttempts(2)
                .waitDuration(Duration.ofMillis(150))
                .build());
    }

    public RequestExecutionResult execute(StressTestConfig config) {
        StressTargetDefinition target = config.getTarget();
        RestTemplate restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(config.getExecution().getTimeoutMillis()))
                .setReadTimeout(Duration.ofMillis(config.getExecution().getTimeoutMillis()))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setAll(target.getHeaders());
        HttpEntity<String> requestEntity = new HttpEntity<String>(target.getPayload(), headers);

        long startedAt = System.nanoTime();
        Callable<ResponseEntity<String>> resilientCall = Retry.decorateCallable(retry,
                () -> restTemplate.exchange(target.getUrl(), target.getMethod(), requestEntity, String.class));
        try {
            ResponseEntity<String> response = resilientCall.call();
            long latencyMillis = toMillis(System.nanoTime() - startedAt);
            boolean success = response.getStatusCode().is2xxSuccessful();
            return new RequestExecutionResult(success, latencyMillis, response.getStatusCode().toString());
        } catch (RestClientException exception) {
            return new RequestExecutionResult(false, toMillis(System.nanoTime() - startedAt), exception.getMessage());
        } catch (Exception exception) {
            return new RequestExecutionResult(false, toMillis(System.nanoTime() - startedAt), exception.getMessage());
        }
    }

    private long toMillis(long durationNanos) {
        return Math.max(1L, durationNanos / 1_000_000L);
    }
}
