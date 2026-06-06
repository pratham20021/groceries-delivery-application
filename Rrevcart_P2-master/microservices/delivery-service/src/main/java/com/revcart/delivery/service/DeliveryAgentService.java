package com.revcart.delivery.service;

import com.revcart.delivery.model.DeliveryAgent;
import com.revcart.delivery.repository.DeliveryAgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DeliveryAgentService {
    
    @Autowired
    private DeliveryAgentRepository agentRepository;
    
    public DeliveryAgent createAgent(DeliveryAgent agent) {
        agent.setStatus("AVAILABLE");
        return agentRepository.save(agent);
    }
    
    public List<DeliveryAgent> getAllAgents() {
        return agentRepository.findAll();
    }
    
    public List<DeliveryAgent> getAvailableAgents() {
        return agentRepository.findByStatus("AVAILABLE");
    }
    
    public DeliveryAgent updateAgentLocation(Long agentId, Double lat, Double lng, Long deliveryId) {
        DeliveryAgent agent = agentRepository.findById(agentId)
            .orElseThrow(() -> new RuntimeException("Agent not found"));
        agent.setCurrentLat(lat);
        agent.setCurrentLng(lng);
        
        System.out.println("Location updated for agent: " + agentId);
        
        return agentRepository.save(agent);
    }
    
    public DeliveryAgent updateAgentStatus(Long agentId, String status) {
        DeliveryAgent agent = agentRepository.findById(agentId)
            .orElseThrow(() -> new RuntimeException("Agent not found"));
        agent.setStatus(status);
        return agentRepository.save(agent);
    }
}
