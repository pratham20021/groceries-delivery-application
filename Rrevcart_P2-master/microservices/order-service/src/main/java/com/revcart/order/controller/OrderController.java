package com.revcart.order.controller;

import com.revcart.order.exception.ResourceNotFoundException;
import com.revcart.order.model.Order;
import com.revcart.order.model.OrderStatus;
import com.revcart.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        System.out.println("\n=== TEST ENDPOINT CALLED ===");
        long totalOrders = orderService.getOrderCount();
        List<Order> allOrders = orderService.getAllOrders();
        
        Map<String, Object> testData = new HashMap<>();
        testData.put("message", "Order service is working");
        testData.put("totalOrders", totalOrders);
        testData.put("ordersFound", allOrders.size());
        testData.put("timestamp", LocalDateTime.now());
        
        System.out.println("Test response: " + testData);
        return ResponseEntity.ok(testData);
    }
    
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        System.out.println("\n=== Received Order Request ===");
        System.out.println("Order userId: " + order.getUserId());
        System.out.println("Order number: " + order.getOrderNumber());
        System.out.println("Order total: " + order.getTotalAmount());
        System.out.println("Coupon code: " + order.getCouponCode());
        System.out.println("Discount: " + order.getDiscountAmount());
        System.out.println("Order items count: " + (order.getOrderItems() != null ? order.getOrderItems().size() : 0));
        
        // Validate required fields
        if (order.getUserId() == null) {
            System.err.println("❌ Error: userId is null");
            throw new IllegalArgumentException("User ID is required");
        }
        
        if (order.getTotalAmount() == null) {
            System.err.println("❌ Error: totalAmount is null");
            throw new IllegalArgumentException("Total amount is required");
        }
        
        // Set customer details if provided
        System.out.println("Customer Name: " + order.getCustomerName());
        System.out.println("Customer Email: " + order.getCustomerEmail());
        System.out.println("Customer Phone: " + order.getCustomerPhone());
        
        Order savedOrder = orderService.createOrder(order);
        System.out.println("✅ Order saved successfully with ID: " + savedOrder.getId());
        return ResponseEntity.ok(savedOrder);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        System.out.println("\n=== Getting orders for user ID: " + userId + " ===");
        List<Order> orders = orderService.getOrdersByUserId(userId);
        System.out.println("Found " + orders.size() + " orders for user " + userId);
        orders.forEach(order -> System.out.println("Order: " + order.getId() + ", Number: " + order.getOrderNumber() + ", Total: " + order.getTotalAmount()));
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/user")
    public ResponseEntity<List<Order>> getCurrentUserOrders(@RequestHeader("userId") Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders(@RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        // This endpoint should only be accessible by admin users
        // For now, we'll restrict it to prevent data leakage
        System.out.println("\n=== Admin access to all orders ===");
        // In a real application, you would check if the user has admin role
        // For security, we'll return empty list unless proper admin authentication is implemented
        return ResponseEntity.ok(new ArrayList<>());
    }
    
    @GetMapping("/admin")
    public ResponseEntity<?> getAdminOrders() {
        return ResponseEntity.ok(orderService.getAllOrdersWithCustomers());
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<Order>> getRecentOrders() {
        List<Order> recentOrders = orderService.getAllOrders();
        return ResponseEntity.ok(recentOrders.stream()
            .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
            .limit(10)
            .toList());
    }
    
    @GetMapping("/debug/count")
    public ResponseEntity<?> getOrderCountDebug() {
        long count = orderService.getOrderCount();
        List<Order> allOrders = orderService.getAllOrders();
        
        Map<String, Object> debug = new HashMap<>();
        debug.put("totalCount", count);
        debug.put("orders", allOrders.stream().map(o -> {
            Map<String, Object> orderInfo = new HashMap<>();
            orderInfo.put("id", o.getId());
            orderInfo.put("userId", o.getUserId());
            orderInfo.put("orderNumber", o.getOrderNumber());
            orderInfo.put("total", o.getTotalAmount());
            orderInfo.put("status", o.getStatus());
            return orderInfo;
        }).toList());
        
        System.out.println("Debug count response: " + debug);
        return ResponseEntity.ok(debug);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getOrderCount() {
        return ResponseEntity.ok(orderService.getOrderCount());
    }
    
    @GetMapping("/revenue")
    public ResponseEntity<Double> getTotalRevenue() {
        return ResponseEntity.ok(orderService.getTotalRevenue());
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/analytics/sales")
    public ResponseEntity<?> getSalesAnalytics() {
        return ResponseEntity.ok(orderService.getSalesAnalytics());
    }
    
    @GetMapping("/analytics/stats")
    public ResponseEntity<?> getOrderStats() {
        return ResponseEntity.ok(orderService.getOrderStats());
    }
    
    @GetMapping("/analytics/today")
    public ResponseEntity<?> getTodayMetrics() {
        return ResponseEntity.ok(orderService.getTodayMetrics());
    }
    
    @PostMapping("/coupons/validate")
    public ResponseEntity<?> validateCoupon(@RequestParam String code, @RequestParam double orderAmount) {
        // Simple coupon validation
        Map<String, Object> response = new HashMap<>();
        
        if ("SAVE10".equals(code) && orderAmount >= 500) {
            response.put("valid", true);
            response.put("discount", orderAmount * 0.1);
            response.put("message", "10% discount applied");
        } else if ("FLAT50".equals(code) && orderAmount >= 200) {
            response.put("valid", true);
            response.put("discount", 50.0);
            response.put("message", "₹50 discount applied");
        } else {
            response.put("valid", false);
            response.put("discount", 0);
            response.put("message", "Invalid coupon or minimum order not met");
        }
        
        return ResponseEntity.ok(response);
    }
}
