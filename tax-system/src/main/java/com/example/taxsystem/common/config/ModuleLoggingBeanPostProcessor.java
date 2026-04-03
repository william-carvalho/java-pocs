package com.example.taxsystem.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class ModuleLoggingBeanPostProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(ModuleLoggingBeanPostProcessor.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        String className = bean.getClass().getName();
        if (className.startsWith("com.example.taxsystem.grocery")
                || className.startsWith("com.example.taxsystem.converter")
                || className.startsWith("com.example.taxsystem.stresstest")) {
            log.debug("Initialized showcase bean: {} ({})", beanName, className);
        }
        return bean;
    }
}
