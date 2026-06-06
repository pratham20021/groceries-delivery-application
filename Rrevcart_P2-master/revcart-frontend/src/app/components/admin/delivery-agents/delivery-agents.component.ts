import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DeliveryService, DeliveryAgent, Delivery } from '../../../services/delivery.service';

@Component({
  selector: 'app-delivery-agents',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './delivery-agents.component.html',
  styleUrls: ['./delivery-agents.component.css']
})
export class DeliveryAgentsComponent implements OnInit {
  agents: DeliveryAgent[] = [];
  deliveries: Delivery[] = [];
  selectedDelivery: Delivery | null = null;
  selectedAgentId: number | null = null;

  constructor(private deliveryService: DeliveryService) {
    console.log('🚀 DeliveryAgentsComponent constructor called');
  }

  ngOnInit() {
    console.log('🚀 DeliveryAgentsComponent ngOnInit called');
    this.loadAgents();
    this.loadDeliveries();
  }

  loadAgents() {
    this.deliveryService.getAllAgents().subscribe({
      next: (agents) => {
        console.log('Loaded agents:', agents);
        this.agents = agents;
      },
      error: (error) => {
        console.error('Error loading agents:', error);
      }
    });
  }

  loadDeliveries() {
    this.deliveryService.getAllDeliveries().subscribe(deliveries => this.deliveries = deliveries);
  }

  updateAgentStatus(agentId: number, status: string) {
    this.deliveryService.updateAgentStatus(agentId, status).subscribe(() => this.loadAgents());
  }

  openReassignModal(delivery: Delivery) {
    this.selectedDelivery = delivery;
    this.selectedAgentId = delivery.deliveryAgentId || null;
  }

  reassignAgent() {
    if (this.selectedDelivery && this.selectedAgentId) {
      this.deliveryService.reassignAgent(this.selectedDelivery.id!, this.selectedAgentId).subscribe(() => {
        this.loadDeliveries();
        this.loadAgents();
        this.selectedDelivery = null;
      });
    }
  }

  getAgentName(agentId: number): string {
    return this.agents.find(a => a.id === agentId)?.name || 'Unknown';
  }

  getStatusClass(status: string): string {
    const map: any = {
      'AVAILABLE': 'status-available',
      'BUSY': 'status-busy',
      'OFFLINE': 'status-offline',
      'ASSIGNED': 'status-assigned',
      'PICKED_UP': 'status-picked',
      'IN_TRANSIT': 'status-transit',
      'DELIVERED': 'status-delivered'
    };
    return map[status] || '';
  }
}
