package com.revcart.auth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "local")
@ComponentScan(basePackages = "com.revcart.auth", 
               excludeFilters = {
                   @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
                                       classes = {SecurityConfig.class})
               })
public class LocalConfig {
}