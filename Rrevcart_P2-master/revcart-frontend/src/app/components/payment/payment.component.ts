import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PaymentService } from '../../services/payment.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="payment-container">
      <h2>Choose Payment Method</h2>
      
      <div class="payment-methods">
        <div class="payment-option" (click)="selectPaymentMethod('card')">
          <input type="radio" name="payment" value="card" [(ngModel)]="selectedPaymentMethod">
          <label>Credit/Debit Card</label>
        </div>

        <div class="payment-option" (click)="selectPaymentMethod('upi')">
          <input type="radio" name="payment" value="upi" [(ngModel)]="selectedPaymentMethod">
          <label>UPI</label>
        </div>

        <div class="payment-option" (click)="selectPaymentMethod('cod')">
          <input type="radio" name="payment" value="cod" [(ngModel)]="selectedPaymentMethod">
          <label>Cash on Delivery</label>
        </div>
      </div>

      <div class="order-summary">
        <h3>Total: ₹{{orderData.total}}</h3>
      </div>

      <button class="pay-button" 
              (click)="processPayment()" 
              [disabled]="!selectedPaymentMethod || processing">
        {{processing ? 'Processing...' : 'Pay ₹' + orderData.total}}
      </button>
    </div>
  `,
  styles: [`
    .payment-container { max-width: 500px; margin: 2rem auto; padding: 2rem; }
    .payment-methods { margin: 2rem 0; }
    .payment-option { padding: 1rem; border: 1px solid #ddd; margin: 1rem 0; cursor: pointer; }
    .payment-option:hover { background: #f5f5f5; }
    .pay-button { width: 100%; padding: 1rem; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
    .pay-button:disabled { background: #ccc; cursor: not-allowed; }
  `]
})
export class PaymentComponent implements OnInit {
  selectedPaymentMethod = '';
  processing = false;
  orderData = { total: 0, items: [] };

  constructor(
    private paymentService: PaymentService,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit() {
    this.orderData.total = this.cartService.getCartTotal();
  }

  selectPaymentMethod(method: string) {
    this.selectedPaymentMethod = method;
  }

  async processPayment() {
    this.processing = true;
    try {
      if (this.selectedPaymentMethod === 'cod') {
        // Handle COD payment
        this.router.navigate(['/order-success']);
      } else {
        // Handle online payment
        this.router.navigate(['/order-success']);
      }
    } catch (error) {
      console.error('Payment failed:', error);
    }
    this.processing = false;
  }
}