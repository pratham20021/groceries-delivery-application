package com.revcart.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    
    @GetMapping("/products/{id}")
    ProductDto getProduct(@PathVariable("id") Long id);
    
    @GetMapping("/products/{id}/stock")
    Integer getProductStock(@PathVariable("id") Long id);
}

class ProductDto {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}