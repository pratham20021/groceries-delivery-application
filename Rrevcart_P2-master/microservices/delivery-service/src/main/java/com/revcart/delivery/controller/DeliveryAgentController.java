package com.revcart.delivery.controller;

import com.revcart.delivery.model.DeliveryAgent;
import com.revcart.delivery.service.DeliveryAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/agents")
public class DeliveryAgentController {
    
    @Autowired
    private DeliveryAgentService agentService;
    
    @PostMapping
    public DeliveryAgent createAgent(@RequestBody DeliveryAgent agent) {
        return agentService.createAgent(agent);
    }
    
    @GetMapping
    public List<DeliveryAgent> getAllAgents() {
        List<DeliveryAgent> agents = agentService.getAllAgents();
        System.out.println("\ud83d\ude9a GET /agents called - Returning " + agents.size() + " agents");
        return agents;
    }
    
    @GetMapping("/available")
    public List<DeliveryAgent> getAvailableAgents() {
        return agentService.getAvailableAgents();
    }
    
    @GetMapping("/count")
    public long getAgentCount() {
        return agentService.getAllAgents().size();
    }
    
    @PutMapping("/{agentId}/location")
    public DeliveryAgent updateLocation(
            @PathVariable Long agentId,
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam Long deliveryId) {
        return agentService.updateAgentLocation(agentId, lat, lng, deliveryId);
    }
    
    @PutMapping("/{agentId}/status")
    public DeliveryAgent updateStatus(@PathVariable Long agentId, @RequestParam String status) {
        return agentService.updateAgentStatus(agentId, status);
    }
}
