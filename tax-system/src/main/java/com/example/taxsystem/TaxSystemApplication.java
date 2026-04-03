package com.example.taxsystem;

import com.example.taxsystem.common.config.ShowcaseProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableConfigurationProperties(ShowcaseProperties.class)
public class TaxSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaxSystemApplication.class, args);
    }
}
