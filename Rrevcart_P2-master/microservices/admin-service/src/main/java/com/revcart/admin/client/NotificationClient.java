package com.revcart.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "notification-service")
public interface NotificationClient {
    
    @PostMapping("/notifications/broadcast")
    Object broadcastNotification(@RequestBody Map<String, Object> request);
}
