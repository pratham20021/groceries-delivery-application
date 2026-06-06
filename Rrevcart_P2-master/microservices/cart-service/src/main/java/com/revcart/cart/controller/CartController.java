package com.revcart.cart.controller;

import com.revcart.cart.exception.ResourceNotFoundException;
import com.revcart.cart.model.CartItem;
import com.revcart.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @GetMapping("/{sessionId}")
    public List<CartItem> getCart(@PathVariable String sessionId) {
        return cartService.getCartItems(sessionId);
    }
    
    @PostMapping("/{sessionId}/add")
    public CartItem addToCart(@PathVariable String sessionId, @RequestBody CartItem item) {
        return cartService.addToCart(sessionId, item.getProductId(), 
                item.getProductName(), item.getPrice(), item.getQuantity());
    }
    
    @DeleteMapping("/item/{itemId}")
    public void removeFromCart(@PathVariable String itemId) {
        cartService.removeFromCart(itemId);
    }
    
    @DeleteMapping("/{sessionId}/clear")
    public void clearCart(@PathVariable String sessionId) {
        cartService.clearCart(sessionId);
    }
    
    @GetMapping("/{sessionId}/total")
    public Double getCartTotal(@PathVariable String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be empty");
        }
        return cartService.getCartTotal(sessionId);
    }
    
    @GetMapping("/test-exception")
    public String testException() {
        throw new ResourceNotFoundException("This is a test exception");
    }
}