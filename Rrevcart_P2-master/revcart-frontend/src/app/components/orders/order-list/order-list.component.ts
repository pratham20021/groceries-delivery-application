import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { OrderService } from '../../../services/order.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="orders-container">
      <div class="container">
        <h1 class="page-title">My <span class="neon-text">Orders</span></h1>
        
        <div class="orders-list" *ngIf="orders.length > 0">
          <div class="order-card glass-card" *ngFor="let order of orders">
            <div class="order-header">
              <div>
                <h3>Order #{{order.orderNumber || order.orderId}}</h3>
                <p class="order-date">{{order.orderDate | date:'medium'}}</p>
              </div>
              <span class="order-status" [class]="order.status?.toLowerCase()">{{order.status || 'PENDING'}}</span>
            </div>
            <div class="order-items">
              <div class="order-item" *ngFor="let item of order.orderItems || order.items">
                <span>{{item.productName || item.name}} x {{item.quantity}}</span>
                <span>₹{{item.price * item.quantity}}</span>
              </div>
            </div>
            <div class="order-details">
              <div class="detail-row" *ngIf="order.couponCode">
                <span>Coupon Applied:</span>
                <span class="coupon-badge">{{order.couponCode}}</span>
              </div>
              <div class="detail-row" *ngIf="order.discountAmount > 0">
                <span>Discount:</span>
                <span class="discount">-₹{{order.discountAmount}}</span>
              </div>
            </div>
            <div class="order-footer">
              <div class="order-info">
                <div class="order-total">
                  <span>Total:</span>
                  <span class="total-amount">₹{{order.totalAmount || order.total}}</span>
                </div>
              </div>
              <div class="order-actions">
                <button class="btn-track" [routerLink]="['/orders', order.id || order.orderId]">
                  <i class="fas fa-map-marker-alt"></i> Track Order
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <div class="no-orders" *ngIf="orders.length === 0">
          <i class="fas fa-shopping-bag"></i>
          <h3>No orders yet</h3>
          <p>Start shopping to see your orders here</p>
          <button class="btn btn-primary" routerLink="/products">Start Shopping</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .orders-container { min-height: 100vh; padding: 120px 0 60px; background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%); }
    .page-title { font-size: 3rem; font-weight: 700; color: white; text-align: center; margin-bottom: 40px; }
    .neon-text { color: #00d4ff; text-shadow: 0 0 20px rgba(0, 212, 255, 0.5); }
    .orders-list { display: flex; flex-direction: column; gap: 20px; }
    .order-card { padding: 25px; background: rgba(255, 255, 255, 0.12); backdrop-filter: blur(20px); border: 1px solid rgba(255, 255, 255, 0.25); border-radius: 15px; }
    .order-header { display: flex; justify-content: space-between; align-items: start; margin-bottom: 20px; }
    .order-header h3 { color: white; margin: 0 0 8px 0; font-size: 1.3rem; }
    .order-date { color: rgba(255, 255, 255, 0.7); margin: 0; font-size: 0.9rem; }
    .order-status { padding: 6px 16px; border-radius: 20px; font-size: 0.85rem; font-weight: 600; text-transform: uppercase; }
    .order-status.pending { background: #f39c12; color: white; }
    .order-status.completed { background: #2ecc71; color: white; }
    .order-status.cancelled { background: #e74c3c; color: white; }
    .order-items { border-top: 1px solid rgba(255, 255, 255, 0.2); border-bottom: 1px solid rgba(255, 255, 255, 0.2); padding: 15px 0; margin-bottom: 15px; }
    .order-item { display: flex; justify-content: space-between; color: white; padding: 8px 0; font-size: 0.95rem; }
    .order-details { padding: 12px 0; border-top: 1px solid rgba(255, 255, 255, 0.1); margin-top: 12px; }
    .detail-row { display: flex; justify-content: space-between; padding: 6px 0; color: rgba(255, 255, 255, 0.8); font-size: 0.9rem; }
    .coupon-badge { background: #2ecc71; color: white; padding: 4px 12px; border-radius: 12px; font-size: 0.85rem; font-weight: 600; }
    .discount { color: #2ecc71; font-weight: 700; }
    .order-footer { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid rgba(255, 255, 255, 0.1); padding-top: 15px; margin-top: 12px; }
    .order-info { display: flex; flex-direction: column; gap: 8px; }
    .order-total { display: flex; gap: 10px; align-items: center; color: rgba(255, 255, 255, 0.8); }
    .total-amount { color: #00d4ff; font-size: 1.3rem; font-weight: 700; }
    .order-actions { display: flex; gap: 10px; }
    .btn-track { padding: 10px 20px; background: #00d4ff; color: white; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; display: flex; align-items: center; gap: 8px; }
    .btn-track:hover { background: #00a3cc; }
    .no-orders { text-align: center; padding: 60px 20px; color: white; }
    .no-orders i { font-size: 4rem; opacity: 0.5; margin-bottom: 20px; }
    .no-orders h3 { font-size: 1.8rem; margin-bottom: 10px; }
    .btn-primary { margin-top: 20px; padding: 12px 30px; background: #00d4ff; color: white; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; }
  `]
})
export class OrderListComponent implements OnInit {
  orders: any[] = [];
  
  constructor(private orderService: OrderService, private http: HttpClient) {}
  
  ngOnInit() {
    console.log('OrderListComponent initialized');
    this.testDirectAPI();
    this.loadOrders();
  }
  
  testDirectAPI() {
    console.log('Testing API endpoints...');
    
    // Test user data
    const user = localStorage.getItem('user');
    console.log('User in localStorage:', user);
    if (user) {
      const parsedUser = JSON.parse(user);
      console.log('Parsed user:', parsedUser);
      console.log('User ID:', parsedUser.id || parsedUser.userId);
    } else {
      console.log('No user found in localStorage, user must be logged in to view orders');
    }
  }
  
  loadOrders() {
    console.log('Loading user-specific orders...');
    
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userId = user.id || user.userId;
    
    if (!userId) {
      console.error('No user ID found, cannot load orders');
      this.orders = [];
      return;
    }
    
    console.log('Loading orders for user ID:', userId);
    
    this.http.get(`http://localhost:8080/api/orders/user/${userId}`).subscribe({
      next: (data: any) => {
        console.log('User orders from database:', data);
        this.orders = Array.isArray(data) ? data : [];
      },
      error: (err) => {
        console.error('Failed to load user orders:', err);
        this.orders = [];
      }
    });
  }
}