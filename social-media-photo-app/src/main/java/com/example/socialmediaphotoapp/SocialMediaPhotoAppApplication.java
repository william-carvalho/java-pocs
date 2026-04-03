package com.example.socialmediaphotoapp;

import com.example.socialmediaphotoapp.config.PhotoStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PhotoStorageProperties.class)
public class SocialMediaPhotoAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialMediaPhotoAppApplication.class, args);
    }
}

