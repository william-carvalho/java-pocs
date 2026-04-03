package com.example.taxsystem.converter.api;

import com.example.taxsystem.common.config.PropertyFileLoader;
import com.example.taxsystem.common.config.ShowcaseProperties;
import com.example.taxsystem.converter.application.ConverterEngine;
import com.example.taxsystem.stresstest.domain.StressTestConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/api/converter")
public class ConverterPreviewController {

    private final ConverterEngine converterEngine;
    private final PropertyFileLoader propertyFileLoader;
    private final ShowcaseProperties showcaseProperties;

    @Value("${spring.application.name}")
    private String applicationName;

    public ConverterPreviewController(ConverterEngine converterEngine,
                                      PropertyFileLoader propertyFileLoader,
                                      ShowcaseProperties showcaseProperties) {
        this.converterEngine = converterEngine;
        this.propertyFileLoader = propertyFileLoader;
        this.showcaseProperties = showcaseProperties;
    }

    @PostMapping("/preview/stress-config")
    public StressTestConfig previewStressConfig(@RequestBody Map<String, Object> payload) {
        return converterEngine.convert(payload, StressTestConfig.class);
    }

    @GetMapping("/samples/properties")
    public Map<String, String> sampleProperties() {
        Properties properties = propertyFileLoader.load(showcaseProperties.getSamples().getPropertiesLocation());
        Map<String, String> response = new LinkedHashMap<String, String>();
        response.put("applicationName", applicationName);
        for (String name : properties.stringPropertyNames()) {
            response.put(name, properties.getProperty(name));
        }
        return response;
    }
}
