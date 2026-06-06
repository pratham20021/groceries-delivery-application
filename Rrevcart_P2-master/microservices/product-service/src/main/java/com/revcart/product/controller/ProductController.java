package com.revcart.product.controller;

import com.revcart.product.exception.ResourceNotFoundException;
import com.revcart.product.model.Product;
import com.revcart.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = productService.getAllProducts(pageable);
        return ResponseEntity.ok(productPage);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProductsList() {
        List<Product> products = productService.getAllProductsList();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        try {
            String decodedCategory = java.net.URLDecoder.decode(category, "UTF-8");
            List<Product> products = productService.getProductsByCategory(decodedCategory);
            return ResponseEntity.ok(products);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Invalid category encoding");
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String q) {
        return ResponseEntity.ok(productService.searchProducts(q));
    }
    
    @GetMapping("/brands")
    public ResponseEntity<List<String>> getAllBrands() {
        return ResponseEntity.ok(productService.getAllBrands());
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getPrice() == null) {
            throw new IllegalArgumentException("Product price is required");
        }
        
        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.getProductById(id)
                .map(existingProduct -> {
                    product.setId(id);
                    Product updatedProduct = productService.saveProduct(product);
                    return ResponseEntity.ok(updatedProduct);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.getProductById(id).isPresent()) {
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } else {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getProductCount() {
        return ResponseEntity.ok(productService.getProductCount());
    }
    
    @GetMapping("/analytics/top")
    public ResponseEntity<?> getTopProducts() {
        return ResponseEntity.ok(productService.getTopProducts());
    }
}