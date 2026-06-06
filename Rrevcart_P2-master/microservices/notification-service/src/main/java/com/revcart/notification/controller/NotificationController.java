package com.revcart.notification.controller;

import com.revcart.notification.model.Notification;
import com.revcart.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.createNotification(notification);
    }
    
    @PostMapping("/send")
    public Notification sendNotification(@RequestBody java.util.Map<String, Object> request) {
        try {
            Notification notification = new Notification();
            notification.setUserId(Long.valueOf(request.get("userId").toString()));
            notification.setType(request.get("type").toString());
            notification.setTitle(request.get("title").toString());
            notification.setMessage(request.get("message").toString());
            notification.setRead(false);
            return notificationService.createNotification(notification);
        } catch (Exception e) {
            System.err.println("Error creating notification: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @GetMapping("/user/{userId}")
    public List<Notification> getNotificationsByUserId(@PathVariable Long userId) {
        return notificationService.getNotificationsByUserId(userId);
    }
    
    @GetMapping("/user")
    public List<Notification> getCurrentUserNotifications(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        Long uid = userId != null ? Long.valueOf(userId) : 1L;
        return notificationService.getNotificationsByUserId(uid);
    }
    
    @GetMapping("/user/{userId}/unread")
    public List<Notification> getUnreadNotifications(@PathVariable Long userId) {
        return notificationService.getUnreadNotifications(userId);
    }
    
    @PatchMapping("/{id}/read")
    public Notification markAsRead(@PathVariable String id) {
        return notificationService.markAsRead(id);
    }
    
    @PatchMapping("/user/{userId}/read-all")
    public void markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
    }
    
    @PutMapping("/mark-all-read")
    public void markAllAsReadForCurrentUser(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        Long uid = userId != null ? Long.valueOf(userId) : 1L;
        notificationService.markAllAsRead(uid);
    }
    
    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
    }
    
    @PostMapping("/broadcast")
    public List<Notification> broadcastNotification(@RequestBody java.util.Map<String, Object> request) {
        String type = request.get("type").toString();
        String title = request.get("title").toString();
        String message = request.get("message").toString();
        return notificationService.broadcastToAllUsers(type, title, message);
    }
}
