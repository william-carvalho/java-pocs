package com.example.filesharesystem;

import com.example.filesharesystem.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class FileShareSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileShareSystemApplication.class, args);
    }
}

