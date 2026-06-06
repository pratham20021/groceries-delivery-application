package com.revcart.product.service;

import com.revcart.product.model.Product;
import com.revcart.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    public List<Product> getAllProductsList() {
        return productRepository.findAll();
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }
    
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    public List<Product> filterProducts(String category, BigDecimal minPrice, BigDecimal maxPrice, String brand) {
        return productRepository.findAll(); // Simplified implementation
    }
    
    public List<String> getAllBrands() {
        return productRepository.findDistinctBrands();
    }
    
    public long getProductCount() {
        return productRepository.count();
    }
    
    public List<Product> getTopProducts() {
        return productRepository.findAll().stream()
            .sorted((p1, p2) -> Integer.compare(p2.getStock(), p1.getStock()))
            .limit(5)
            .toList();
    }
}