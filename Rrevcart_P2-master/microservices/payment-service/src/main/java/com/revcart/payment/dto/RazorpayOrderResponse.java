package com.revcart.payment.dto;

public class RazorpayOrderResponse {
    private String razorpayOrderId;
    private String razorpayKeyId;
    private Double amount;
    private String currency;
    private Long orderId;
    
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    
    public String getRazorpayKeyId() { return razorpayKeyId; }
    public void setRazorpayKeyId(String razorpayKeyId) { this.razorpayKeyId = razorpayKeyId; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}
