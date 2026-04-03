package com.example.hibernateslowquerydetector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.hibernateslowquerydetector.config.SlowQueryProperties;

@SpringBootApplication
@EnableConfigurationProperties(SlowQueryProperties.class)
public class HibernateSlowQueryDetectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(HibernateSlowQueryDetectorApplication.class, args);
    }
}

