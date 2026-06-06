package com.revcart.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    
    @GetMapping("/orders/count")
    long getOrderCount();
    
    @GetMapping("/orders/revenue")
    double getTotalRevenue();
    
    @GetMapping("/orders/recent")
    List<?> getRecentOrders();
    
    @GetMapping("/orders/analytics/sales")
    Object getSalesAnalytics();
    
    @GetMapping("/orders/analytics/stats")
    Object getOrderStats();
    
    @GetMapping("/orders/analytics/today")
    Object getTodayMetrics();
}
