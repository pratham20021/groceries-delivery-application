package com.revcart.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/users/count")
    long getUserCount();
}
