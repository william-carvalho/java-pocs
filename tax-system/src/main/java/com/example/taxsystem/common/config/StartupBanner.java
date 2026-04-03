package com.example.taxsystem.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartupBanner {

    private static final Logger log = LoggerFactory.getLogger(StartupBanner.class);

    public StartupBanner() {
        log.info("Tax System showcase bootstrapped");
    }
}
