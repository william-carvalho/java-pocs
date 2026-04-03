package com.example.taxsystem.common.config;

import com.example.taxsystem.common.exception.TechnicalException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class PropertyFileLoader {

    private final ResourceLoader resourceLoader;

    public PropertyFileLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Properties load(String location) {
        Resource resource = resourceLoader.getResource(location);
        Properties properties = new Properties();
        try (InputStream inputStream = resource.getInputStream()) {
            properties.load(inputStream);
            return properties;
        } catch (IOException exception) {
            throw new TechnicalException("Failed to load properties from " + location, exception);
        }
    }
}
