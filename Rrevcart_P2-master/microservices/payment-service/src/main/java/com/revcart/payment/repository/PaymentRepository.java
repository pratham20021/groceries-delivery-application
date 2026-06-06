package com.revcart.payment.repository;

import com.revcart.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);
    List<Payment> findByUserId(Long userId);
    List<Payment> findByStatus(Payment.PaymentStatus status);
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}
