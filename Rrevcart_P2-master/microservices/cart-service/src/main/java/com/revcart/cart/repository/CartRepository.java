package com.revcart.cart.repository;

import com.revcart.cart.model.CartItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartRepository extends CrudRepository<CartItem, String> {
    List<CartItem> findBySessionId(String sessionId);
    void deleteBySessionId(String sessionId);
}