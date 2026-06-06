package com.revcart.delivery.service;

import com.revcart.delivery.model.Delivery;
import com.revcart.delivery.model.DeliveryAgent;
import com.revcart.delivery.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryService {
    
    @Autowired
    private DeliveryRepository deliveryRepository;
    
    @Autowired
    private DeliveryAgentService agentService;
    
    public Delivery assignDelivery(Delivery delivery) {
        if (delivery.getDeliveryAgentId() == null) {
            List<DeliveryAgent> availableAgents = agentService.getAvailableAgents();
            if (availableAgents.isEmpty()) {
                throw new RuntimeException("No available delivery agents");
            }
            delivery.setDeliveryAgentId(availableAgents.get(0).getId());
        }
        
        delivery.setStatus(Delivery.DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now());
        agentService.updateAgentStatus(delivery.getDeliveryAgentId(), "BUSY");
        return deliveryRepository.save(delivery);
    }
    
    public Delivery updateStatus(Long id, Delivery.DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Delivery not found"));
        delivery.setStatus(status);
        
        if (status == Delivery.DeliveryStatus.PICKED_UP) {
            delivery.setPickedUpAt(LocalDateTime.now());
        } else if (status == Delivery.DeliveryStatus.DELIVERED) {
            delivery.setDeliveredAt(LocalDateTime.now());
            agentService.updateAgentStatus(delivery.getDeliveryAgentId(), "AVAILABLE");
        } else if (status == Delivery.DeliveryStatus.CANCELLED) {
            agentService.updateAgentStatus(delivery.getDeliveryAgentId(), "AVAILABLE");
        }
        
        return deliveryRepository.save(delivery);
    }
    
    public List<Delivery> getDeliveriesByAgent(Long agentId) {
        return deliveryRepository.findByDeliveryAgentId(agentId);
    }
    
    public List<Delivery> getDeliveriesByOrder(Long orderId) {
        return deliveryRepository.findByOrderId(orderId);
    }
    
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }
    
    public Delivery reassignAgent(Long deliveryId, Long newAgentId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new RuntimeException("Delivery not found"));
        
        Long oldAgentId = delivery.getDeliveryAgentId();
        if (oldAgentId != null) {
            agentService.updateAgentStatus(oldAgentId, "AVAILABLE");
        }
        
        delivery.setDeliveryAgentId(newAgentId);
        agentService.updateAgentStatus(newAgentId, "BUSY");
        return deliveryRepository.save(delivery);
    }
}
