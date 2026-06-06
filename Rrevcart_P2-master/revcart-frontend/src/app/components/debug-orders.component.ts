import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-debug-orders',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="padding: 20px; background: #f5f5f5; margin: 20px;">
      <h2>Order Debug Panel</h2>
      
      <div style="margin: 10px 0;">
        <button (click)="testOrderService()" style="margin: 5px; padding: 10px;">Test Order Service</button>
        <button (click)="getAllOrders()" style="margin: 5px; padding: 10px;">Get All Orders</button>
        <button (click)="getUserOrders()" style="margin: 5px; padding: 10px;">Get User Orders</button>
        <button (click)="checkUser()" style="margin: 5px; padding: 10px;">Check User Data</button>
      </div>
      
      <div style="background: white; padding: 15px; margin: 10px 0; border-radius: 5px;">
        <h3>Results:</h3>
        <pre>{{ results | json }}</pre>
      </div>
      
      <div style="background: white; padding: 15px; margin: 10px 0; border-radius: 5px;">
        <h3>User Data:</h3>
        <pre>{{ userData | json }}</pre>
      </div>
    </div>
  `
})
export class DebugOrdersComponent implements OnInit {
  results: any = {};
  userData: any = {};

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.checkUser();
  }

  testOrderService() {
    console.log('Testing order service...');
    this.http.get('http://localhost:8080/api/orders/test').subscribe({
      next: (data) => {
        console.log('Test response:', data);
        this.results = { test: data };
      },
      error: (err) => {
        console.error('Test failed:', err);
        this.results = { error: err.message, details: err };
      }
    });
  }

  getAllOrders() {
    console.log('Getting all orders...');
    this.http.get('http://localhost:8080/api/orders/all').subscribe({
      next: (data) => {
        console.log('All orders:', data);
        this.results = { allOrders: data };
      },
      error: (err) => {
        console.error('Get all orders failed:', err);
        this.results = { error: err.message, details: err };
      }
    });
  }

  getUserOrders() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userId = user.id || 1;
    
    console.log('Getting user orders for ID:', userId);
    this.http.get(`http://localhost:8080/api/orders/user/${userId}`).subscribe({
      next: (data) => {
        console.log('User orders:', data);
        this.results = { userOrders: data, userId: userId };
      },
      error: (err) => {
        console.error('Get user orders failed:', err);
        this.results = { error: err.message, userId: userId, details: err };
      }
    });
  }

  checkUser() {
    const user = localStorage.getItem('user');
    const token = localStorage.getItem('token');
    
    this.userData = {
      hasUser: !!user,
      hasToken: !!token,
      userRaw: user,
      userParsed: user ? JSON.parse(user) : null,
      tokenLength: token ? token.length : 0
    };
    
    console.log('User data check:', this.userData);
  }
}