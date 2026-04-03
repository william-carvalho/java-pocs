package com.example.dontpadclone.config;

import com.example.dontpadclone.dto.CreatePadRequest;
import com.example.dontpadclone.repository.PadRepository;
import com.example.dontpadclone.service.PadService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitialDataLoader {

    @Bean
    CommandLineRunner loadInitialData(PadRepository padRepository, PadService padService) {
        return args -> {
            if (padRepository.existsBySlug("welcome")) {
                return;
            }

            CreatePadRequest request = new CreatePadRequest();
            request.setSlug("welcome");
            request.setContent("Welcome to DontPad Clone");
            padService.create(request);
        };
    }
}

