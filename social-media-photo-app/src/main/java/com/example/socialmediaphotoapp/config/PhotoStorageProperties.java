package com.example.socialmediaphotoapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "photo.storage")
public class PhotoStorageProperties {

    private String path = "./storage/photos";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

