package com.revcart.payment.service;

import com.revcart.payment.exception.PaymentException;
import com.revcart.payment.exception.ResourceNotFoundException;
import com.revcart.payment.model.Payment;
import com.revcart.payment.repository.PaymentRepository;
import com.revcart.payment.dto.RazorpayOrderRequest;
import com.revcart.payment.dto.RazorpayOrderResponse;
import com.revcart.payment.dto.RazorpayPaymentVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private RazorpayService razorpayService;
    
    public RazorpayOrderResponse createRazorpayOrder(Long orderId, Long userId, Double amount) {
        try {
            RazorpayOrderRequest request = new RazorpayOrderRequest();
            request.setOrderId(orderId);
            request.setAmount(amount);
            request.setCurrency("INR");
            
            RazorpayOrderResponse response = razorpayService.createOrder(request);
            
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setUserId(userId);
            payment.setAmount(new java.math.BigDecimal(amount));
            payment.setStatus(Payment.PaymentStatus.PENDING);
            payment.setRazorpayOrderId(response.getRazorpayOrderId());
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);
            
            return response;
        } catch (Exception e) {
            throw new PaymentException("Failed to create payment order: " + e.getMessage());
        }
    }
    
    public Payment verifyAndUpdatePayment(RazorpayPaymentVerification verification) {
        boolean isValid = razorpayService.verifyPayment(verification);
        
        Payment payment = paymentRepository.findByRazorpayOrderId(verification.getRazorpayOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with order ID: " + verification.getRazorpayOrderId()));
        
        if (isValid) {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setRazorpayPaymentId(verification.getRazorpayPaymentId());
            payment.setRazorpaySignature(verification.getRazorpaySignature());
            payment.setTransactionId(verification.getRazorpayPaymentId());
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
        }
        
        return paymentRepository.save(payment);
    }
    
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }
    
    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
    
    public List<Payment> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId);
    }
    
    public Payment refundPayment(Long id) {
        Payment payment = getPaymentById(id);
        if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
            payment.setStatus(Payment.PaymentStatus.REFUNDED);
            return paymentRepository.save(payment);
        }
        throw new PaymentException("Only successful payments can be refunded");
    }
}
