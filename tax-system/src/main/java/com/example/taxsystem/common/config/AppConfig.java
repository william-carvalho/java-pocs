package com.example.taxsystem.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;

@Configuration
public class AppConfig {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeGsonAdapter())
                .create();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, ShowcaseProperties showcaseProperties) {
        Duration timeout = Duration.ofMillis(showcaseProperties.getStress().getDefaultTimeoutMillis());
        return builder
                .setConnectTimeout(timeout)
                .setReadTimeout(timeout)
                .build();
    }

    @Bean(name = "stressAsyncExecutor")
    public Executor stressAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("stress-async-");
        executor.initialize();
        return executor;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public StressExecutionContext stressExecutionContext() {
        return new StressExecutionContext();
    }

    @Bean
    @Profile("!test")
    public StartupBanner startupBanner() {
        return new StartupBanner();
    }
}
