package com.revcart.order.service;

import com.revcart.order.dto.OrderWithCustomerDTO;
import com.revcart.order.model.Order;
import com.revcart.order.model.OrderStatus;
import com.revcart.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Order createOrder(Order order) {
        order.setOrderDate(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        
        // Set order reference for all order items
        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(item -> item.setOrder(order));
        }
        
        Order savedOrder = orderRepository.save(order);
        System.out.println("Order saved with ID: " + savedOrder.getId());
        
        // Send notification
        sendOrderConfirmationNotification(savedOrder);
        
        return savedOrder;
    }
    
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    public List<Order> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        orders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
        return orders;
    }
    
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        orders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
        return orders;
    }
    
    public List<OrderWithCustomerDTO> getAllOrdersWithCustomers() {
        List<Order> orders = getAllOrders();
        return orders.stream().map(order -> {
            try {
                Map<String, Object> user = restTemplate.getForObject(
                    "http://localhost:8082/users/" + order.getUserId(), Map.class);
                return new OrderWithCustomerDTO(
                    order,
                    user != null ? (String) user.get("name") : "Unknown",
                    user != null ? (String) user.get("email") : "N/A",
                    user != null ? (String) user.get("phone") : "N/A"
                );
            } catch (Exception e) {
                return new OrderWithCustomerDTO(order, "Unknown", "N/A", "N/A");
            }
        }).collect(Collectors.toList());
    }
    
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        
        // Send notification for status change
        if (oldStatus != status) {
            sendStatusChangeNotification(updatedOrder, status);
        }
        
        return updatedOrder;
    }
    
    private void sendStatusChangeNotification(Order order, OrderStatus status) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("userId", order.getUserId());
            notification.put("type", "ORDER_STATUS");
            
            switch (status) {
                case PROCESSING:
                    notification.put("title", "Order Processing");
                    notification.put("message", "Your order #" + order.getOrderNumber() + " is being processed");
                    break;
                case SHIPPED:
                    notification.put("title", "Order Shipped");
                    notification.put("message", "Your order #" + order.getOrderNumber() + " has been shipped");
                    break;
                case DELIVERED:
                    notification.put("title", "Order Delivered");
                    notification.put("message", "Your order #" + order.getOrderNumber() + " has been delivered");
                    break;
                case CANCELLED:
                    notification.put("title", "Order Cancelled");
                    notification.put("message", "Your order #" + order.getOrderNumber() + " has been cancelled");
                    break;
                default:
                    return;
            }
            
            restTemplate.postForObject("http://localhost:8087/notifications/send", notification, String.class);
        } catch (Exception e) {
            System.err.println("Failed to send status notification: " + e.getMessage());
        }
    }
    
    public void cancelOrder(Long id) {
        Order order = getOrderById(id);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
    
    public long getOrderCount() {
        return orderRepository.count();
    }
    
    public double getTotalRevenue() {
        return orderRepository.findAll().stream()
            .mapToDouble(order -> order.getTotalAmount().doubleValue())
            .sum();
    }
    
    private void sendOrderConfirmationNotification(Order order) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("userId", order.getUserId());
            notification.put("type", "ORDER_CONFIRMATION");
            notification.put("title", "Order Confirmed!");
            notification.put("message", "Your order #" + order.getOrderNumber() + " has been confirmed. Total: ₹" + order.getTotalAmount());
            
            restTemplate.postForObject("http://localhost:8087/notifications/send", notification, String.class);
            System.out.println("Notification sent for order: " + order.getId());
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
    
    @Scheduled(fixedRate = 60000)
    public void updateOrderStatuses() {
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        LocalDateTime now = LocalDateTime.now();
        
        for (Order order : pendingOrders) {
            LocalDateTime orderTime = order.getOrderDate();
            long minutesSinceOrder = java.time.Duration.between(orderTime, now).toMinutes();
            
            if (minutesSinceOrder >= 5) {
                order.setStatus(OrderStatus.DELIVERED);
                orderRepository.save(order);
                sendDeliveryNotification(order);
                System.out.println("Order " + order.getId() + " marked as DELIVERED");
            }
        }
    }
    
    private void sendDeliveryNotification(Order order) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("userId", order.getUserId());
            notification.put("type", "ORDER_DELIVERED");
            notification.put("title", "Order Delivered!");
            notification.put("message", "Your order #" + order.getOrderNumber() + " has been delivered successfully!");
            
            restTemplate.postForObject("http://localhost:8087/notifications/send", notification, String.class);
            System.out.println("Delivery notification sent for order: " + order.getId());
        } catch (Exception e) {
            System.err.println("Failed to send delivery notification: " + e.getMessage());
        }
    }
    
    public Map<String, Object> getSalesAnalytics() {
        List<Order> orders = orderRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        
        double today = orders.stream()
            .filter(o -> o.getOrderDate().toLocalDate().equals(now.toLocalDate()))
            .mapToDouble(o -> o.getTotalAmount().doubleValue()).sum();
        
        double week = orders.stream()
            .filter(o -> o.getOrderDate().isAfter(now.minusDays(7)))
            .mapToDouble(o -> o.getTotalAmount().doubleValue()).sum();
        
        double month = orders.stream()
            .filter(o -> o.getOrderDate().isAfter(now.minusDays(30)))
            .mapToDouble(o -> o.getTotalAmount().doubleValue()).sum();
        
        Map<String, Object> sales = new HashMap<>();
        sales.put("today", today);
        sales.put("week", week);
        sales.put("month", month);
        return sales;
    }
    
    public Map<String, Long> getOrderStats() {
        List<Order> orders = orderRepository.findAll();
        Map<String, Long> stats = new HashMap<>();
        stats.put("pending", orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count());
        stats.put("processing", orders.stream().filter(o -> o.getStatus() == OrderStatus.PROCESSING).count());
        stats.put("delivered", orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count());
        stats.put("cancelled", orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count());
        return stats;
    }
    
    public Map<String, Object> getTodayMetrics() {
        List<Order> orders = orderRepository.findAll();
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        
        long todayOrders = orders.stream()
            .filter(o -> o.getOrderDate().isAfter(today)).count();
        
        double todayRevenue = orders.stream()
            .filter(o -> o.getOrderDate().isAfter(today))
            .mapToDouble(o -> o.getTotalAmount().doubleValue()).sum();
        
        long pendingDeliveries = orders.stream()
            .filter(o -> o.getStatus() != OrderStatus.DELIVERED && o.getStatus() != OrderStatus.CANCELLED)
            .count();
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("todayOrders", todayOrders);
        metrics.put("todayRevenue", todayRevenue);
        metrics.put("pendingDeliveries", pendingDeliveries);
        return metrics;
    }
}
