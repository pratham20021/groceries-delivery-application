package com.revcart.payment.dto;

public class RazorpayOrderRequest {
    private Long orderId;
    private Double amount;
    private String currency = "INR";
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
