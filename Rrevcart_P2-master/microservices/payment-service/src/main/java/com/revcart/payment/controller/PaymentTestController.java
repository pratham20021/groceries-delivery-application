package com.revcart.payment.controller;

import com.revcart.payment.exception.PaymentException;
import com.revcart.payment.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment-test")
public class PaymentTestController {

    @GetMapping("/not-found")
    public String testNotFound() {
        throw new ResourceNotFoundException("Test payment not found");
    }

    @GetMapping("/payment-error")
    public String testPaymentError() {
        throw new PaymentException("Test payment failed");
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