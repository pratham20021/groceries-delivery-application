package com.revcart.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/test")
    public String test() {
        return "Auth Service is running in local mode!";
    }
    
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}