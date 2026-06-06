import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface DashboardStats {
  totalProducts: number;
  totalOrders: number;
  totalUsers: number;
  totalRevenue: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = `${environment.apiUrl}/admin`;

  constructor(public http: HttpClient) {}

  getDashboardStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.apiUrl}/dashboard`);
  }

  getRecentOrders(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/orders/recent`);
  }
  
  getSalesAnalytics(): Observable<any> {
    return this.http.get(`${this.apiUrl}/analytics/sales`);
  }
  
  getTopProducts(): Observable<any> {
    return this.http.get(`${this.apiUrl}/analytics/top-products`);
  }
  
  getOrderStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/analytics/order-stats`);
  }
  
  getTodayMetrics(): Observable<any> {
    return this.http.get(`${this.apiUrl}/metrics/today`);
  }
  
  broadcastNotification(type: string, title: string, message: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/notifications/broadcast`, { type, title, message });
  }
}