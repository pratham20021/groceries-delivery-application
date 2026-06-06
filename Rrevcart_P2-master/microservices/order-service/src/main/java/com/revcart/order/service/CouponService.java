package com.revcart.order.service;

import com.revcart.order.model.Coupon;
import com.revcart.order.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponService {
    private final CouponRepository couponRepository;
    
    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }
    
    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        if (couponRepository.existsByCode(coupon.getCode())) {
            throw new RuntimeException("Coupon code already exists");
        }
        return couponRepository.save(coupon);
    }
    
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }
    
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));
    }
    
    @Transactional
    public Coupon updateCoupon(Long id, Coupon couponDetails) {
        Coupon coupon = getCouponById(id);
        coupon.setDescription(couponDetails.getDescription());
        coupon.setDiscountType(couponDetails.getDiscountType());
        coupon.setDiscountValue(couponDetails.getDiscountValue());
        coupon.setMinOrderAmount(couponDetails.getMinOrderAmount());
        coupon.setMaxDiscountAmount(couponDetails.getMaxDiscountAmount());
        coupon.setValidFrom(couponDetails.getValidFrom());
        coupon.setValidUntil(couponDetails.getValidUntil());
        coupon.setUsageLimit(couponDetails.getUsageLimit());
        coupon.setActive(couponDetails.getActive());
        return couponRepository.save(coupon);
    }
    
    @Transactional
    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }
    
    public Coupon validateAndGetCoupon(String code, Double orderAmount) {
        Coupon coupon = couponRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Invalid coupon code"));
        
        if (!coupon.getActive()) {
            throw new RuntimeException("Coupon is inactive");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            throw new RuntimeException("Coupon not yet valid");
        }
        
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            throw new RuntimeException("Coupon has expired");
        }
        
        if (coupon.getMinOrderAmount() != null && orderAmount < coupon.getMinOrderAmount()) {
            throw new RuntimeException("Order amount does not meet minimum requirement");
        }
        
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new RuntimeException("Coupon usage limit reached");
        }
        
        return coupon;
    }
    
    public Double calculateDiscount(Coupon coupon, Double orderAmount) {
        Double discount = 0.0;
        
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            discount = orderAmount * (coupon.getDiscountValue() / 100);
        } else if (coupon.getDiscountType() == Coupon.DiscountType.FIXED) {
            discount = coupon.getDiscountValue();
        }
        
        if (coupon.getMaxDiscountAmount() != null && discount > coupon.getMaxDiscountAmount()) {
            discount = coupon.getMaxDiscountAmount();
        }
        
        return Math.min(discount, orderAmount);
    }
    
    @Transactional
    public void incrementUsageCount(Long couponId) {
        Coupon coupon = getCouponById(couponId);
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
    }
}
