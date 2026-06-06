package com.revcart.payment.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.revcart.payment.dto.RazorpayOrderRequest;
import com.revcart.payment.dto.RazorpayOrderResponse;
import com.revcart.payment.dto.RazorpayPaymentVerification;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {
    
    @Autowired
    private RazorpayClient razorpayClient;
    
    @Value("${razorpay.key-id}")
    private String keyId;
    
    @Value("${razorpay.key-secret}")
    private String keySecret;
    
    public RazorpayOrderResponse createOrder(RazorpayOrderRequest request) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(request.getAmount() * 100)); // Amount in paise
        orderRequest.put("currency", request.getCurrency());
        orderRequest.put("receipt", "order_" + request.getOrderId());
        
        Order order = razorpayClient.orders.create(orderRequest);
        
        RazorpayOrderResponse response = new RazorpayOrderResponse();
        response.setRazorpayOrderId(order.get("id"));
        response.setRazorpayKeyId(keyId);
        response.setAmount(request.getAmount());
        response.setCurrency(request.getCurrency());
        response.setOrderId(request.getOrderId());
        
        return response;
    }
    
    public boolean verifyPayment(RazorpayPaymentVerification verification) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", verification.getRazorpayOrderId());
            options.put("razorpay_payment_id", verification.getRazorpayPaymentId());
            options.put("razorpay_signature", verification.getRazorpaySignature());
            
            return Utils.verifyPaymentSignature(options, keySecret);
        } catch (Exception e) {
            return false;
        }
    }
}
