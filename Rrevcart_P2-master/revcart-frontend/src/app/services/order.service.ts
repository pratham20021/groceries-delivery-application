import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Order {
  id: number;
  userId: number;
  items: OrderItem[];
  totalAmount: number;
  status: string;
  deliveryAddress: string;
  orderDate: string;
}

export interface RecentOrder {
  orderId: string;
  customerName: string;
  amount: number;
  status: string;
  createdAt: string;
}

export interface OrderItem {
  productId: number;
  productName: string;
  quantity: number;
  price: number;
}

export interface CreateOrderRequest {
  items: { productId: number; quantity: number; }[];
  deliveryAddress: string;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = `${environment.apiUrl}/orders`;

  constructor(private http: HttpClient) {}

  createOrder(orderData: CreateOrderRequest): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, orderData);
  }

  getUserOrders(): Observable<Order[]> {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userId = user.id || user.userId;
    
    console.log('Getting orders for user:', userId);
    console.log('User object from localStorage:', user);
    
    if (!userId) {
      console.error('No valid user ID found in localStorage');
      throw new Error('User must be logged in to view orders');
    }
    
    return this.http.get<Order[]>(`${this.apiUrl}/user/${userId}`);
  }

  getAllOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/admin`);
  }
  
  getAdminOrders(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin`);
  }

  updateOrderStatus(orderId: number, status: string): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${orderId}/status`, { status });
  }

  getRecentOrders(): Observable<RecentOrder[]> {
    return this.http.get<RecentOrder[]>(`${this.apiUrl}/recent`);
  }
}