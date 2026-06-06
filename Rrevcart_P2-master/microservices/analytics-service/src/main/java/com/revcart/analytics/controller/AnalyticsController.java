package com.revcart.analytics.controller;

import com.revcart.analytics.model.Analytics;
import com.revcart.analytics.repository.AnalyticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    
    @Autowired
    private AnalyticsRepository repository;
    
    @PostMapping("/test")
    public Analytics createTest() {
        Analytics analytics = new Analytics();
        analytics.setEventType("TEST");
        analytics.setUserId("test-user");
        analytics.setData("Test analytics to create database");
        return repository.save(analytics);
    }
    
    @GetMapping("/test")
    public String test() {
        return "Analytics service is running! Count: " + repository.count();
    }
}
