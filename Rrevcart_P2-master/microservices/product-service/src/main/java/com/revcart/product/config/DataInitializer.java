package com.revcart.product.config;

import com.revcart.product.model.Product;
import com.revcart.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            initializeProducts();
        }
    }
    
    private void initializeProducts() {
        // Electronics - Smartphones
        createProduct("iPhone 15 Pro", "Latest Apple smartphone with A17 Pro chip", new BigDecimal("999.99"), 50, 
            "https://m.media-amazon.com/images/I/81SigpJN1KL._AC_SL1500_.jpg", "Electronics", "Apple", new BigDecimal("4.8"));
        
        createProduct("Samsung Galaxy S24", "Flagship Android phone with AI features", new BigDecimal("899.99"), 45, 
            "https://m.media-amazon.com/images/I/71h2wOQ0r5L._AC_SL1500_.jpg", "Electronics", "Samsung", new BigDecimal("4.7"));
        
        createProduct("Google Pixel 8", "Pure Android experience with best camera", new BigDecimal("699.99"), 40, 
            "https://m.media-amazon.com/images/I/71fJI+AvqBL._AC_SL1500_.jpg", "Electronics", "Google", new BigDecimal("4.6"));
        
        // Electronics - Laptops
        createProduct("MacBook Pro 16", "Powerful laptop with M3 chip", new BigDecimal("2499.99"), 30, 
            "https://m.media-amazon.com/images/I/61aUBxqc5PL._AC_SL1500_.jpg", "Electronics", "Apple", new BigDecimal("4.9"));
        
        createProduct("Dell XPS 15", "Premium Windows laptop for professionals", new BigDecimal("1799.99"), 35, 
            "https://m.media-amazon.com/images/I/71TlKbH-8PL._AC_SL1500_.jpg", "Electronics", "Dell", new BigDecimal("4.7"));
        
        createProduct("HP Spectre x360", "Convertible laptop with touchscreen", new BigDecimal("1499.99"), 25, 
            "https://m.media-amazon.com/images/I/71YcP7RLJyL._AC_SL1500_.jpg", "Electronics", "HP", new BigDecimal("4.5"));
        
        // Electronics - Headphones
        createProduct("Sony WH-1000XM5", "Premium noise cancelling headphones", new BigDecimal("399.99"), 60, 
            "https://m.media-amazon.com/images/I/61vEu+q+hfL._AC_SL1500_.jpg", "Electronics", "Sony", new BigDecimal("4.8"));
        
        createProduct("AirPods Pro", "Apple wireless earbuds with ANC", new BigDecimal("249.99"), 80, 
            "https://m.media-amazon.com/images/I/61SUj2aKoEL._AC_SL1500_.jpg", "Electronics", "Apple", new BigDecimal("4.7"));
        
        // Fashion - Men
        createProduct("Men's Casual Shirt", "Cotton blend casual shirt", new BigDecimal("49.99"), 100, 
            "https://m.media-amazon.com/images/I/71VVs9p7SQL._AC_UY879_.jpg", "Fashion", "Generic", new BigDecimal("4.3"));
        
        createProduct("Men's Denim Jeans", "Classic fit blue jeans", new BigDecimal("79.99"), 80, 
            "https://m.media-amazon.com/images/I/71YKpoO6AQL._AC_UY879_.jpg", "Fashion", "Levi's", new BigDecimal("4.5"));
        
        createProduct("Men's Leather Jacket", "Genuine leather biker jacket", new BigDecimal("299.99"), 30, 
            "https://m.media-amazon.com/images/I/71pGufWYHxL._AC_UY879_.jpg", "Fashion", "Generic", new BigDecimal("4.6"));
        
        // Fashion - Women
        createProduct("Women's Summer Dress", "Floral print summer dress", new BigDecimal("69.99"), 70, 
            "https://m.media-amazon.com/images/I/71jWkM8HPSL._AC_UY879_.jpg", "Fashion", "Generic", new BigDecimal("4.4"));
        
        createProduct("Women's Handbag", "Designer leather handbag", new BigDecimal("199.99"), 40, 
            "https://m.media-amazon.com/images/I/81hCJZj5SQL._AC_UY879_.jpg", "Fashion", "Generic", new BigDecimal("4.7"));
        
        // Home & Kitchen
        createProduct("Coffee Maker", "Programmable drip coffee maker", new BigDecimal("89.99"), 50, 
            "https://m.media-amazon.com/images/I/71zny7ql-UL._AC_SL1500_.jpg", "Home & Kitchen", "Generic", new BigDecimal("4.4"));
        
        createProduct("Blender", "High-speed blender for smoothies", new BigDecimal("129.99"), 45, 
            "https://m.media-amazon.com/images/I/71JWx7LRyJL._AC_SL1500_.jpg", "Home & Kitchen", "Generic", new BigDecimal("4.5"));
        
        createProduct("Air Fryer", "Digital air fryer with 8 presets", new BigDecimal("149.99"), 55, 
            "https://m.media-amazon.com/images/I/71NzNXzDGrL._AC_SL1500_.jpg", "Home & Kitchen", "Generic", new BigDecimal("4.6"));
        
        // Sports & Outdoors
        createProduct("Yoga Mat", "Non-slip exercise yoga mat", new BigDecimal("29.99"), 100, 
            "https://m.media-amazon.com/images/I/81-Gu+QHVUL._AC_SL1500_.jpg", "Sports", "Generic", new BigDecimal("4.5"));
        
        createProduct("Dumbbell Set", "Adjustable dumbbell set 20kg", new BigDecimal("199.99"), 40, 
            "https://m.media-amazon.com/images/I/71qU+HdZ5LL._AC_SL1500_.jpg", "Sports", "Generic", new BigDecimal("4.6"));
        
        createProduct("Running Shoes", "Lightweight running shoes", new BigDecimal("119.99"), 70, 
            "https://m.media-amazon.com/images/I/61N4DP6snBL._AC_UY695_.jpg", "Sports", "Nike", new BigDecimal("4.7"));
        
        // Books
        createProduct("The Great Novel", "Bestselling fiction book", new BigDecimal("19.99"), 150, 
            "https://m.media-amazon.com/images/I/81QuEGw8VPL._SL1500_.jpg", "Books", "Generic", new BigDecimal("4.5"));
        
        createProduct("Programming Guide", "Complete programming reference", new BigDecimal("49.99"), 80, 
            "https://m.media-amazon.com/images/I/71m1wiFhfkL._SL1360_.jpg", "Books", "Generic", new BigDecimal("4.8"));
        
        // Food & Snacks
        createProduct("Salted Peanuts", "Roasted and salted peanuts 500g", new BigDecimal("5.99"), 200, 
            "https://m.media-amazon.com/images/I/81xnWVLhOyL._SL1500_.jpg", "Food", "Generic", new BigDecimal("4.3"));
        
        createProduct("Popcorn", "Butter flavored microwave popcorn", new BigDecimal("4.99"), 150, 
            "https://m.media-amazon.com/images/I/81QpkRbBvyL._SL1500_.jpg", "Food", "Generic", new BigDecimal("4.4"));
        
        createProduct("Salted Cashews", "Premium roasted cashews 400g", new BigDecimal("12.99"), 100, 
            "https://m.media-amazon.com/images/I/81J7eXQGZyL._SL1500_.jpg", "Food", "Generic", new BigDecimal("4.6"));
        
        createProduct("Rusk", "Crispy wheat rusk biscuits", new BigDecimal("3.99"), 180, 
            "https://m.media-amazon.com/images/I/71vqHWKJOyL._SL1500_.jpg", "Food", "Generic", new BigDecimal("4.2"));
        
        // Seafood
        createProduct("Rohu Fish", "Fresh rohu fish 1kg", new BigDecimal("8.99"), 50, 
            "https://m.media-amazon.com/images/I/71KqVZ8XJSL._SL1500_.jpg", "Seafood", "Generic", new BigDecimal("4.5"));
        
        createProduct("Smoked Salmon", "Premium smoked salmon 200g", new BigDecimal("15.99"), 60, 
            "https://m.media-amazon.com/images/I/81rqVvhN8LL._SL1500_.jpg", "Seafood", "Generic", new BigDecimal("4.7"));
        
        createProduct("Shrimp", "Fresh jumbo shrimp 500g", new BigDecimal("18.99"), 40, 
            "https://m.media-amazon.com/images/I/71xZnZWLjyL._SL1500_.jpg", "Seafood", "Generic", new BigDecimal("4.6"));
        
        // Dairy
        createProduct("Skim Milk", "Low-fat skim milk 1 liter", new BigDecimal("2.99"), 120, 
            "https://m.media-amazon.com/images/I/61hL3AWJdyL._SL1500_.jpg", "Dairy", "Generic", new BigDecimal("4.4"));
    }
    
    private void createProduct(String name, String description, BigDecimal price, int stock, 
                               String imageUrl, String category, String brand, BigDecimal rating) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setInitialStock(stock);
        product.setCurrentStock(stock);
        product.setImageUrl(imageUrl);
        product.setCategory(category);
        product.setBrand(brand);
        product.setActive(true);
        product.setRating(rating);
        productRepository.save(product);
    }
}
