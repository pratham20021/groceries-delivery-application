package com.revcart.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    
    @GetMapping("/products/count")
    long getProductCount();
    
    @GetMapping("/products/analytics/top")
    Object getTopProducts();
}
