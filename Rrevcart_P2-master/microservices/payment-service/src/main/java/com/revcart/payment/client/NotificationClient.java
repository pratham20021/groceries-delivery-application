package com.revcart.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "notification-service")
public interface NotificationClient {
    
    @PostMapping("/notifications/send")
    void sendNotification(@RequestBody NotificationRequest request);
}

class NotificationRequest {
    private String userId;
    private String type;
    private String message;
    
    public NotificationRequest() {}
    public NotificationRequest(String userId, String type, String message) {
        this.userId = userId;
        this.type = type;
        this.message = message;
    }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
