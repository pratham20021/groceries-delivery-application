package com.revcart.notification.service;

import com.revcart.notification.model.Notification;
import com.revcart.notification.repository.NotificationRepository;
import com.revcart.notification.client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        return notificationRepository.save(notification);
    }
    
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }
    
    public Notification markAsRead(String id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
    
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadFalse(userId);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }
    
    public void deleteNotification(String id) {
        notificationRepository.deleteById(id);
    }
    
    @Autowired
    private UserClient userClient;
    
    public List<Notification> broadcastToAllUsers(String type, String title, String message) {
        List<Long> userIds = userClient.getAllUsers().stream()
            .map(user -> user.getId())
            .collect(Collectors.toList());
        
        return userIds.stream().map(userId -> {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setType(type);
            notification.setTitle(title);
            notification.setMessage(message);
            return createNotification(notification);
        }).collect(Collectors.toList());
    }
}
