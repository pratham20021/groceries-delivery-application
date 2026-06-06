package com.revcart.admin.controller;

import com.revcart.admin.dto.DashboardStats;
import com.revcart.admin.service.AdminService;
import com.revcart.admin.client.NotificationClient;
import com.revcart.admin.model.Coupon;
import com.revcart.admin.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.math.BigDecimal;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private NotificationClient notificationClient;
    
    @Autowired
    private CouponRepository couponRepository;
    
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }
    
    @GetMapping("/orders/recent")
    public ResponseEntity<?> getRecentOrders() {
        return ResponseEntity.ok(adminService.getRecentOrders());
    }
    
    @GetMapping("/analytics/sales")
    public ResponseEntity<?> getSalesAnalytics() {
        return ResponseEntity.ok(adminService.getSalesAnalytics());
    }
    
    @GetMapping("/analytics/top-products")
    public ResponseEntity<?> getTopProducts() {
        return ResponseEntity.ok(adminService.getTopProducts());
    }
    
    @GetMapping("/analytics/order-stats")
    public ResponseEntity<?> getOrderStats() {
        return ResponseEntity.ok(adminService.getOrderStats());
    }
    
    @GetMapping("/metrics/today")
    public ResponseEntity<?> getTodayMetrics() {
        return ResponseEntity.ok(adminService.getTodayMetrics());
    }
    
    @PostMapping("/notifications/broadcast")
    public ResponseEntity<?> broadcastNotification(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(notificationClient.broadcastNotification(request));
    }
    
    @GetMapping("/coupons")
    public ResponseEntity<?> getCoupons() {
        return ResponseEntity.ok(couponRepository.findAll());
    }
    
    @PostMapping("/coupons")
    public ResponseEntity<?> createCoupon(@RequestBody Map<String, Object> request) {
        Coupon coupon = new Coupon();
        coupon.setCode((String) request.get("code"));
        coupon.setDescription((String) request.get("description"));
        coupon.setDiscountType((String) request.get("discountType"));
        coupon.setDiscountValue(new BigDecimal(request.get("discountValue").toString()));
        coupon.setActive(true);
        
        Coupon saved = couponRepository.save(coupon);
        return ResponseEntity.ok(saved);
    }
}
