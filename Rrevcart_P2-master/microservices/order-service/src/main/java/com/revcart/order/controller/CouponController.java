package com.revcart.order.controller;

import com.revcart.order.model.Coupon;
import com.revcart.order.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupons")
public class CouponController {
    private final CouponService couponService;
    
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }
    
    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.createCoupon(coupon));
    }
    
    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.getCouponById(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.updateCoupon(id, coupon));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateCoupon(
            @RequestParam String code,
            @RequestParam Double orderAmount) {
        try {
            Coupon coupon = couponService.validateAndGetCoupon(code, orderAmount);
            Double discount = couponService.calculateDiscount(coupon, orderAmount);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("coupon", coupon);
            response.put("discount", discount);
            response.put("finalAmount", orderAmount - discount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
