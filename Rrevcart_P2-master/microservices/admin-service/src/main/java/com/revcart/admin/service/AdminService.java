package com.revcart.admin.service;

import com.revcart.admin.client.OrderServiceClient;
import com.revcart.admin.client.ProductServiceClient;
import com.revcart.admin.client.UserServiceClient;
import com.revcart.admin.dto.DashboardStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminService {
    
    @Autowired
    private ProductServiceClient productServiceClient;
    
    @Autowired
    private OrderServiceClient orderServiceClient;
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    public DashboardStats getDashboardStats() {
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            
            String productHost = System.getenv().getOrDefault("PRODUCT_SERVICE_HOST", "localhost:8083");
            String orderHost = System.getenv().getOrDefault("ORDER_SERVICE_HOST", "localhost:8085");
            String userHost = System.getenv().getOrDefault("USER_SERVICE_HOST", "localhost:8082");
            
            Long totalProducts = restTemplate.getForObject("http://" + productHost + "/products/count", Long.class);
            Long totalOrders = restTemplate.getForObject("http://" + orderHost + "/orders/count", Long.class);
            Long totalUsers = restTemplate.getForObject("http://" + userHost + "/users/count", Long.class);
            Double totalRevenue = restTemplate.getForObject("http://" + orderHost + "/orders/revenue", Double.class);
            
            System.out.println("Dashboard Stats - Products: " + totalProducts + ", Orders: " + totalOrders + ", Users: " + totalUsers + ", Revenue: " + totalRevenue);
            
            return new DashboardStats(
                totalProducts != null ? totalProducts : 0,
                totalOrders != null ? totalOrders : 0,
                totalUsers != null ? totalUsers : 0,
                totalRevenue != null ? totalRevenue : 0.0
            );
        } catch (Exception e) {
            System.err.println("Error fetching dashboard stats: " + e.getMessage());
            e.printStackTrace();
            return new DashboardStats(0, 0, 0, 0.0);
        }
    }
    
    public List<?> getRecentOrders() {
        return orderServiceClient.getRecentOrders();
    }
    
    public Object getSalesAnalytics() {
        return orderServiceClient.getSalesAnalytics();
    }
    
    public Object getTopProducts() {
        return productServiceClient.getTopProducts();
    }
    
    public Object getOrderStats() {
        return orderServiceClient.getOrderStats();
    }
    
    public Object getTodayMetrics() {
        try {
            return orderServiceClient.getTodayMetrics();
        } catch (Exception e) {
            System.err.println("Error fetching today metrics: " + e.getMessage());
            java.util.Map<String, Object> fallback = new java.util.HashMap<>();
            fallback.put("todayOrders", 0);
            fallback.put("todayRevenue", 0.0);
            fallback.put("pendingDeliveries", 0);
            return fallback;
        }
    }
}
