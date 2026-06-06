package com.revcart.analytics.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "analytics")
public class Analytics {
    
    @Id
    private String id;
    private String eventType;
    private String userId;
    private String productId;
    private String data;
    private LocalDateTime timestamp;
    
    public Analytics() {
        this.timestamp = LocalDateTime.now();
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
