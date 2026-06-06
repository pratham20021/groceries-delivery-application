package com.revcart.auth.controller;

import com.revcart.auth.exception.AuthenticationException;
import com.revcart.auth.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth-test")
public class AuthTestController {

    @GetMapping("/not-found")
    public String testNotFound() {
        throw new ResourceNotFoundException("Test user not found");
    }

    @GetMapping("/auth-error")
    public String testAuthError() {
        throw new AuthenticationException("Test authentication failed");
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