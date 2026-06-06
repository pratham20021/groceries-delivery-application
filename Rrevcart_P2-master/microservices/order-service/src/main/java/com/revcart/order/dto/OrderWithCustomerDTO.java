package com.revcart.order.dto;

import com.revcart.order.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderWithCustomerDTO {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private LocalDateTime orderDate;
    private LocalDateTime createdAt;
    private int itemCount;
    
    public OrderWithCustomerDTO(Order order, String customerName, String customerEmail, String customerPhone) {
        this.id = order.getId();
        this.orderNumber = order.getOrderNumber();
        this.userId = order.getUserId();
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus().toString();
        this.shippingAddress = order.getShippingAddress();
        this.orderDate = order.getOrderDate();
        this.createdAt = order.getCreatedAt();
        this.itemCount = order.getOrderItems() != null ? order.getOrderItems().size() : 0;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
}
