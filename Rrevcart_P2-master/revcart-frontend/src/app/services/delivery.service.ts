import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface DeliveryAgent {
  id?: number;
  name: string;
  phone: string;
  email: string;
  vehicleType: string;
  status: string;
  currentLat?: number;
  currentLng?: number;
}

export interface Delivery {
  id?: number;
  orderId: number;
  deliveryAgentId?: number;
  status: string;
  pickupAddress?: string;
  deliveryAddress: string;
  assignedAt?: string;
  pickedUpAt?: string;
  deliveredAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {
  private apiUrl = `${environment.apiUrl}/delivery`;
  private agentUrl = `${environment.apiUrl}/agents`;

  constructor(private http: HttpClient) {
    console.log('DeliveryService initialized');
    console.log('Agent URL:', this.agentUrl);
    console.log('Delivery URL:', this.apiUrl);
  }

  getAllAgents(): Observable<DeliveryAgent[]> {
    console.log('Fetching agents from:', this.agentUrl);
    return this.http.get<DeliveryAgent[]>(this.agentUrl);
  }

  getAvailableAgents(): Observable<DeliveryAgent[]> {
    return this.http.get<DeliveryAgent[]>(`${this.agentUrl}/available`);
  }

  updateAgentStatus(agentId: number, status: string): Observable<DeliveryAgent> {
    return this.http.put<DeliveryAgent>(`${this.agentUrl}/${agentId}/status?status=${status}`, {});
  }

  getAllDeliveries(): Observable<Delivery[]> {
    return this.http.get<Delivery[]>(`${this.apiUrl}/all`);
  }

  reassignAgent(deliveryId: number, agentId: number): Observable<Delivery> {
    return this.http.put<Delivery>(`${this.apiUrl}/${deliveryId}/reassign/${agentId}`, {});
  }

  updateDeliveryStatus(deliveryId: number, status: string): Observable<Delivery> {
    return this.http.patch<Delivery>(`${this.apiUrl}/${deliveryId}/status?status=${status}`, {});
  }
}
