package com.revcart.delivery.controller;

import com.revcart.delivery.model.Delivery;
import com.revcart.delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/delivery")
@CrossOrigin(origins = "*")
public class DeliveryController {
    
    @Autowired
    private DeliveryService deliveryService;
    
    @PostMapping("/assign")
    public ResponseEntity<Delivery> assignDelivery(@RequestBody Delivery delivery) {
        return ResponseEntity.ok(deliveryService.assignDelivery(delivery));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Delivery> updateStatus(
            @PathVariable Long id,
            @RequestParam Delivery.DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.updateStatus(id, status));
    }
    
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Delivery>> getDeliveriesByAgent(@PathVariable Long agentId) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByAgent(agentId));
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Delivery>> getDeliveriesByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByOrder(orderId));
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }
    
    @PutMapping("/{deliveryId}/reassign/{agentId}")
    public ResponseEntity<Delivery> reassignAgent(
            @PathVariable Long deliveryId,
            @PathVariable Long agentId) {
        return ResponseEntity.ok(deliveryService.reassignAgent(deliveryId, agentId));
    }
}
