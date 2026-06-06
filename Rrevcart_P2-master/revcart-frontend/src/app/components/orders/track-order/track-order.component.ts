import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { interval, Subscription } from 'rxjs';
import { OrderService } from '../../../services/order.service';

@Component({
  selector: 'app-track-order',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="track-container">
      <div class="container">
        <div class="track-header">
          <h1>Track Your Order</h1>
          <p>Order ID: {{orderId}}</p>
        </div>
        
        <div class="order-progress">
          <div class="progress-step" [class.active]="currentStep >= 1" [class.completed]="currentStep > 1">
            <div class="step-icon">
              <i class="fas fa-shopping-cart"></i>
            </div>
            <div class="step-info">
              <h3>Order Placed</h3>
              <p>{{orderPlacedTime | date:'medium'}}</p>
            </div>
          </div>
          
          <div class="progress-line" [class.active]="currentStep >= 2"></div>
          
          <div class="progress-step" [class.active]="currentStep >= 2" [class.completed]="currentStep > 2">
            <div class="step-icon">
              <i class="fas fa-box"></i>
            </div>
            <div class="step-info">
              <h3>Order Packed</h3>
              <p *ngIf="currentStep >= 2">{{getStepTime(2) | date:'medium'}}</p>
            </div>
          </div>
          
          <div class="progress-line" [class.active]="currentStep >= 3"></div>
          
          <div class="progress-step" [class.active]="currentStep >= 3" [class.completed]="currentStep > 3">
            <div class="step-icon">
              <i class="fas fa-truck"></i>
            </div>
            <div class="step-info">
              <h3>Out for Delivery</h3>
              <p *ngIf="currentStep >= 3">{{getStepTime(3) | date:'medium'}}</p>
            </div>
          </div>
          
          <div class="progress-line" [class.active]="currentStep >= 4"></div>
          
          <div class="progress-step" [class.active]="currentStep >= 4">
            <div class="step-icon">
              <i class="fas fa-check-circle"></i>
            </div>
            <div class="step-info">
              <h3>Delivered</h3>
              <p *ngIf="currentStep >= 4">{{getStepTime(4) | date:'medium'}}</p>
            </div>
          </div>
        </div>
        
        <div class="order-details" *ngIf="orderData">
          <h3>Order Details</h3>
          <div class="detail-item">
            <span>Order Number:</span>
            <strong>{{orderData.orderNumber}}</strong>
          </div>
          <div class="detail-item">
            <span>Total Amount:</span>
            <strong>₹{{orderData.totalAmount}}</strong>
          </div>
          <div class="detail-item" *ngIf="orderData.couponCode">
            <span>Coupon Applied:</span>
            <strong>{{orderData.couponCode}} (-₹{{orderData.discountAmount}})</strong>
          </div>
          <div class="detail-item">
            <span>Status:</span>
            <strong class="status-badge">{{orderData.status}}</strong>
          </div>
        </div>
        
        <div class="estimated-time" *ngIf="currentStep < 4">
          <p>Estimated delivery: {{estimatedDelivery | date:'medium'}}</p>
        </div>
        
        <div class="back-button">
          <button routerLink="/orders">← Back to Orders</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .track-container {
      min-height: 100vh;
      padding: 120px 20px 60px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
    }
    .track-header {
      text-align: center;
      margin-bottom: 60px;
      color: white;
    }
    .order-progress {
      display: flex;
      align-items: center;
      justify-content: center;
      max-width: 800px;
      margin: 0 auto;
      padding: 40px;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 20px;
    }
    .progress-step {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      color: #ccc;
    }
    .progress-step.active {
      color: #00d4ff;
    }
    .step-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      background: #444;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      margin-bottom: 15px;
    }
    .progress-step.active .step-icon {
      background: #00d4ff;
      color: white;
    }
    .progress-line {
      width: 100px;
      height: 3px;
      background: #444;
      margin: 0 20px;
    }
    .progress-line.active {
      background: #00d4ff;
    }
    .order-details {
      max-width: 600px;
      margin: 40px auto;
      padding: 30px;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 15px;
      color: white;
    }
    .order-details h3 {
      margin-bottom: 20px;
      color: #00d4ff;
    }
    .detail-item {
      display: flex;
      justify-content: space-between;
      padding: 12px 0;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    }
    .status-badge {
      background: #2ecc71;
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 0.9rem;
    }
    .estimated-time {
      text-align: center;
      margin-top: 40px;
      color: white;
    }
    .back-button {
      text-align: center;
      margin-top: 30px;
    }
    .back-button button {
      padding: 12px 30px;
      background: #00d4ff;
      color: white;
      border: none;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
    }
  `]
})
export class TrackOrderComponent implements OnInit, OnDestroy {
  orderId: string = '';
  currentStep: number = 1;
  orderPlacedTime: Date = new Date();
  estimatedDelivery: Date = new Date();
  orderData: any = null;
  private subscription: Subscription = new Subscription();

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.orderId = params['id'];
      if (this.orderId) {
        this.loadOrderDetails();
      }
    });

    this.orderPlacedTime = new Date();
    this.estimatedDelivery = new Date(Date.now() + 45 * 60 * 1000);

    this.subscription = interval(60000).subscribe(() => {
      this.updateOrderStatus();
    });
  }
  
  loadOrderDetails() {
    this.orderService.getUserOrders().subscribe({
      next: (orders) => {
        this.orderData = orders.find((o: any) => o.id == this.orderId);
        if (this.orderData) {
          this.orderPlacedTime = new Date(this.orderData.orderDate);
          this.mapStatusToStep(this.orderData.status);
        }
      },
      error: (err) => console.error('Error loading order:', err)
    });
  }
  
  mapStatusToStep(status: string) {
    switch (status) {
      case 'PENDING': this.currentStep = 1; break;
      case 'PROCESSING': this.currentStep = 2; break;
      case 'SHIPPED': this.currentStep = 3; break;
      case 'DELIVERED': this.currentStep = 4; break;
      default: this.currentStep = 1;
    }
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  updateOrderStatus() {
    const elapsed = Date.now() - this.orderPlacedTime.getTime();
    const minutes = elapsed / (1000 * 60);

    if (minutes >= 4) {
      this.currentStep = 4;
    } else if (minutes >= 3) {
      this.currentStep = 3;
    } else if (minutes >= 1) {
      this.currentStep = 2;
    }
  }

  getStepTime(step: number): Date {
    const baseTime = this.orderPlacedTime.getTime();
    switch (step) {
      case 2: return new Date(baseTime + 1 * 60 * 1000);
      case 3: return new Date(baseTime + 3 * 60 * 1000);
      case 4: return new Date(baseTime + 4 * 60 * 1000);
      default: return this.orderPlacedTime;
    }
  }
}