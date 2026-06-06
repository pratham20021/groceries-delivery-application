package com.revcart.product.config;

import com.revcart.product.model.Product;
import com.revcart.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
public class ImageFixRunner implements CommandLineRunner {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public void run(String... args) {
        fixProductImages();
    }
    
    private void fixProductImages() {
        List<Product> allProducts = productRepository.findAll();
        
        for (Product product : allProducts) {
            String name = product.getName().toLowerCase();
            String currentUrl = product.getImageUrl();
            
            if (currentUrl == null || currentUrl.isEmpty() || currentUrl.contains("placeholder")) {
                if (name.contains("salted peanuts") || name.contains("peanuts")) {
                    product.setImageUrl("https://m.media-amazon.com/images/I/81xnWVLhOyL._SL1500_.jpg");
                    productRepository.save(product);
                } else if (name.contains("popcorn")) {
                    product.setImageUrl("https://m.media-amazon.com/images/I/81QpkRbBvyL._SL1500_.jpg");
                    productRepository.save(product);
                } else if (name.contains("salted cashews") || name.contains("cashews")) {
                    product.setImageUrl("https://m.media-amazon.com/images/I/81J7eXQGZyL._SL1500_.jpg");
                    productRepository.save(product);
                } else if (name.contains("rusk")) {
                    product.setImageUrl("https://m.media-amazon.com/images/I/71vqHWKJOyL._SL1500_.jpg");
                    productRepository.save(product);
                } else if (name.contains("rohu")) {
                    product.setImageUrl("https://m.media-amazon.com/images/I/71KqVZ8XJSL._SL1500_.jpg");
                    productRepository.save(product);
                } else if (name.contains("smoked salmon") || name.contains("salmon")) {
                    product.setImageUrl("https://m.media-amazon.com/images/I/81rqVvhN8LL._SL1500_.jpg");
                    productRepository.save(product);
                } else if (name.contains("shrimp")) {
                    product.setImageUrl("https://m.media-amazon.com/images/I/71xZnZWLjyL._SL1500_.jpg");
                    productRepository.save(product);
                } else if (name.contains("skim milk") || name.contains("milk")) {
                    product.setImageUrl("https://m.media-amazon.com/images/I/61hL3AWJdyL._SL1500_.jpg");
                    productRepository.save(product);
                }
            }
        }
    }
}
