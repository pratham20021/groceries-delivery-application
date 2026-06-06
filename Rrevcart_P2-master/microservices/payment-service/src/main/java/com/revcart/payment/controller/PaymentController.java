package com.revcart.payment.controller;

import com.revcart.payment.dto.RazorpayOrderResponse;
import com.revcart.payment.dto.RazorpayPaymentVerification;
import com.revcart.payment.exception.ResourceNotFoundException;
import com.revcart.payment.model.Payment;
import com.revcart.payment.service.PaymentService;
import com.revcart.payment.service.RazorpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private RazorpayService razorpayService;
    
    @PostMapping("/create-order")
    public ResponseEntity<RazorpayOrderResponse> createOrder(@RequestParam Long orderId, @RequestParam Long userId, @RequestParam Double amount) {
        if (orderId == null || userId == null || amount == null || amount <= 0) {
            throw new IllegalArgumentException("Invalid order parameters");
        }
        return ResponseEntity.ok(paymentService.createRazorpayOrder(orderId, userId, amount));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }
    
    @PostMapping("/{id}/refund")
    public ResponseEntity<Payment> refundPayment(@PathVariable Long id) {
        Payment payment = paymentService.refundPayment(id);
        return ResponseEntity.ok(payment);
    }
    
    @PostMapping("/verify")
    public ResponseEntity<Payment> verifyPayment(@RequestBody RazorpayPaymentVerification verification) {
        if (verification == null) {
            throw new IllegalArgumentException("Payment verification data is required");
        }
        Payment payment = paymentService.verifyAndUpdatePayment(verification);
        return ResponseEntity.ok(payment);
    }
}
