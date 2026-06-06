package com.revcart.cart.service;

import com.revcart.cart.model.CartItem;
import com.revcart.cart.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    public List<CartItem> getCartItems(String sessionId) {
        return cartRepository.findBySessionId(sessionId);
    }
    
    public CartItem addToCart(String sessionId, Long productId, String productName, Double price, Integer quantity) {
        CartItem cartItem = new CartItem(sessionId, productId, productName, price, quantity);
        return cartRepository.save(cartItem);
    }
    
    public void removeFromCart(String itemId) {
        cartRepository.deleteById(itemId);
    }
    
    public void clearCart(String sessionId) {
        cartRepository.deleteBySessionId(sessionId);
    }
    
    public Double getCartTotal(String sessionId) {
        List<CartItem> items = getCartItems(sessionId);
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}