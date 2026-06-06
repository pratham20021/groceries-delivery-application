package com.revcart.cart.controller;

import com.revcart.cart.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/not-found")
    public String testNotFound() {
        throw new ResourceNotFoundException("Test resource not found");
    }

    @GetMapping("/bad-request")
    public String testBadRequest() {
        throw new IllegalArgumentException("Test bad request");
    }

    @GetMapping("/server-error")
    public String testServerError() {
        throw new RuntimeException("Test server error");
    }
}