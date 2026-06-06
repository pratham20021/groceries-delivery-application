import { Component, OnInit, ViewChild, ElementRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService, CartItem } from '../../services/cart.service';
import { CheckoutService } from '../../services/checkout.service';



interface OrderData {
  customerName: string;
  email: string;
  phone: string;
  address: {
    street: string;
    city: string;
    postalCode: string;
  };
  deliveryInstructions: string;
  paymentMethod: 'card' | 'upi' | 'cod';
}

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {
  
  orderData: OrderData = {
    customerName: '',
    email: '',
    phone: '',
    address: {
      street: '',
      city: '',
      postalCode: ''
    },
    deliveryInstructions: '',
    paymentMethod: 'card'
  };
  
  cartItems: CartItem[] = [];
  
  baseDeliveryFee = 30;
  isProcessing = false;
  showUpiModal = false;
  showInvoice = false;
  invoiceData: any = null;
  
  couponCode = '';
  appliedCoupon: any = null;
  couponDiscount = 0;
  couponError = '';
  availableCoupons: any[] = [];
  showCoupons = false;
  
  @ViewChild('invoiceContent') invoiceContent!: ElementRef;
  
  constructor(
    private router: Router,
    private cartService: CartService,
    private checkoutService: CheckoutService,
    private ngZone: NgZone
  ) {}
  
  ngOnInit() {
    this.cartService.getCartItems().subscribe(items => {
      this.cartItems = items;
    });
    this.loadAvailableCoupons();
  }
  
  loadAvailableCoupons() {
    const coupons = JSON.parse(localStorage.getItem('coupons') || '[]');
    this.availableCoupons = coupons.filter((c: any) => c.active);
    
    // If no coupons in localStorage, add default ones
    if (this.availableCoupons.length === 0) {
      const defaultCoupons = [
        { id: 1, code: 'SAVE10', description: '10% off', discountType: 'PERCENTAGE', discountValue: 10, active: true },
        { id: 2, code: 'FLAT50', description: '₹50 off', discountType: 'FIXED', discountValue: 50, active: true }
      ];
      localStorage.setItem('coupons', JSON.stringify(defaultCoupons));
      this.availableCoupons = defaultCoupons;
    }
  }
  
  toggleCoupons() {
    this.showCoupons = !this.showCoupons;
  }
  
  selectCoupon(code: string) {
    this.couponCode = code;
    this.applyCoupon();
    this.showCoupons = false;
  }
  
  selectPaymentMethod(method: 'card' | 'upi' | 'cod') {
    this.orderData.paymentMethod = method;
  }
  
  closeUpiModal() {
    this.showUpiModal = false;
  }
  
  completeUpiPayment() {
    this.showUpiModal = false;
    this.generateInvoice();
  }
  
  initiateRazorpayPayment() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userId = user.id || 1;
    const orderId = Math.floor(Math.random() * 1000000);
    const amount = this.getTotal();
    
    fetch(`http://localhost:8086/payments/create-order?orderId=${orderId}&userId=${userId}&amount=${amount}`, {
      method: 'POST'
    })
    .then(res => res.json())
    .then(data => {
      const options = {
        key: data.razorpayKeyId,
        amount: data.amount * 100,
        currency: data.currency,
        name: 'RevCart',
        description: 'Order Payment',
        order_id: data.razorpayOrderId,
        handler: (response: any) => {
          this.verifyRazorpayPayment(response);
        },
        prefill: {
          name: this.orderData.customerName,
          email: this.orderData.email,
          contact: this.orderData.phone
        },
        theme: { color: '#528FF0' },
        modal: {
          ondismiss: () => {
            this.ngZone.run(() => {
              setTimeout(() => {
                this.generateInvoice();
                this.isProcessing = false;
              }, 500);
            });
          }
        }
      };
      
      const rzp = new (window as any).Razorpay(options);
      rzp.on('payment.failed', () => {
        this.ngZone.run(() => {
          setTimeout(() => {
            this.generateInvoice();
            this.isProcessing = false;
          }, 500);
        });
      });
      rzp.open();
    })
    .catch(error => {
      alert('Error initiating payment: ' + error.message);
      this.isProcessing = false;
    });
  }
  
  verifyRazorpayPayment(response: any) {
    console.log('Payment response received:', response);
    fetch('http://localhost:8086/payments/verify', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        razorpayOrderId: response.razorpay_order_id,
        razorpayPaymentId: response.razorpay_payment_id,
        razorpaySignature: response.razorpay_signature
      })
    })
    .then(res => res.json())
    .then(payment => {
      console.log('Payment verified:', payment);
    })
    .catch(error => {
      console.log('Verification error:', error);
    })
    .finally(() => {
      this.ngZone.run(() => {
        this.generateInvoice();
        this.isProcessing = false;
      });
    });
  }
  
  closeInvoice() {
    this.showInvoice = false;
    this.router.navigate(['/order-success']);
  }
  
  downloadInvoice() {
    window.print();
  }
  
  getSubtotal(): number {
    return Math.round(this.cartItems.reduce((total, item) => total + (item.price * item.quantity), 0));
  }
  
  getGST(): number {
    return Math.round(this.getSubtotal() * 0.18);
  }
  
  getDeliveryFee(): number {
    return this.getSubtotal() > 199 ? 0 : this.baseDeliveryFee;
  }
  
  getTotal(): number {
    const subtotal = this.getSubtotal();
    const delivery = this.getDeliveryFee();
    const gst = this.getGST();
    const discount = this.couponDiscount || 0;
    return Math.max(0, Math.round(subtotal + delivery + gst - discount));
  }
  
  applyCoupon() {
    if (!this.couponCode.trim()) {
      this.couponError = 'Please enter a coupon code';
      return;
    }
    
    const coupons = JSON.parse(localStorage.getItem('coupons') || '[]');
    const coupon = coupons.find((c: any) => c.code === this.couponCode && c.active);
    
    if (coupon) {
      const orderAmount = this.getSubtotal() + this.getDeliveryFee() + this.getGST();
      let discount = 0;
      
      if (coupon.discountType === 'PERCENTAGE') {
        discount = orderAmount * (coupon.discountValue / 100);
      } else {
        discount = coupon.discountValue;
      }
      
      this.appliedCoupon = coupon;
      this.couponDiscount = Math.round(discount);
      this.couponError = '';
      alert(`Coupon applied! You saved ₹${this.couponDiscount}`);
    } else {
      this.couponError = 'Invalid coupon code';
      this.appliedCoupon = null;
      this.couponDiscount = 0;
    }
  }
  
  removeCoupon() {
    this.couponCode = '';
    this.appliedCoupon = null;
    this.couponDiscount = 0;
    this.couponError = '';
  }
  
  getItemCount(): number {
    return this.cartItems.reduce((total, item) => total + item.quantity, 0);
  }
  
  onSubmit() {
    if (this.isProcessing) return;
    
    this.isProcessing = true;
    
    // Process order with stock validation
    this.checkoutService.processOrder(this.cartItems).subscribe(result => {
      if (!result.success) {
        alert(result.message);
        this.isProcessing = false;
        return;
      }
      
      // Handle payment based on method
      if (this.orderData.paymentMethod === 'upi' || this.orderData.paymentMethod === 'card') {
        this.initiateRazorpayPayment();
      } else {
        // COD - directly generate invoice
        setTimeout(() => {
          this.generateInvoice();
          this.isProcessing = false;
        }, 1000);
      }
    });
  }
  
  generateInvoice() {
    const orderId = this.generateOrderId();
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userId = user.id;
    
    this.invoiceData = {
      orderId: orderId,
      userId: userId ? Number(userId) : null,
      customerName: this.orderData.customerName,
      email: this.orderData.email,
      phone: this.orderData.phone,
      address: this.orderData.address,
      paymentMethod: this.orderData.paymentMethod,
      items: this.cartItems,
      subtotal: this.getSubtotal(),
      deliveryFee: this.getDeliveryFee(),
      gst: this.getGST(),
      couponCode: this.appliedCoupon?.code || null,
      couponDiscount: this.couponDiscount,
      total: this.getTotal(),
      orderDate: new Date(),
      estimatedDelivery: new Date(Date.now() + 45 * 60 * 1000)
    };
    
    // Format order for backend
    const orderPayload = {
      userId: this.invoiceData.userId || 1,
      orderNumber: this.invoiceData.orderId,
      totalAmount: Number(this.invoiceData.total),
      status: 'PENDING',
      shippingAddress: `${this.invoiceData.address.street}, ${this.invoiceData.address.city}, ${this.invoiceData.address.postalCode}`,
      orderDate: new Date().toISOString(),
      couponCode: this.invoiceData.couponCode || null,
      discountAmount: Number(this.invoiceData.couponDiscount) || 0,
      customerName: this.invoiceData.customerName,
      customerEmail: this.invoiceData.email,
      customerPhone: this.invoiceData.phone,
      orderItems: this.invoiceData.items.map((item: any) => ({
        productId: item.id,
        productName: item.name,
        quantity: item.quantity,
        price: Number(item.price)
      }))
    };
    
    console.log('User from localStorage:', JSON.parse(localStorage.getItem('user') || '{}'));
    console.log('Saving order with payload:', orderPayload);
    
    // Save order to backend
    this.checkoutService.saveOrder(orderPayload).subscribe({
      next: (response) => {
        console.log('Order saved:', response.id);
      },
      error: (error) => {
        console.error('Order save failed:', error.message);
      }
    });
    
    // Clear cart after successful order
    this.cartService.clearCart();
    
    // Store in session storage for success page
    sessionStorage.setItem('orderSummary', JSON.stringify(this.invoiceData));
    
    // Show success notification
    this.showOrderNotification();
    
    this.showInvoice = true;
  }
  
  private generateOrderId(): string {
    const timestamp = Date.now().toString();
    const random = Math.random().toString(36).substr(2, 5).toUpperCase();
    return `RC${timestamp.slice(-6)}${random}`;
  }
  
  private showOrderNotification() {
    const notification = {
      title: 'Order Confirmed!',
      message: `Your order #${this.invoiceData.orderId} has been confirmed. Total: ₹${this.invoiceData.total}`,
      type: 'success'
    };
    
    // Show browser notification if permitted
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification(notification.title, {
        body: notification.message,
        icon: '/favicon.ico'
      });
    }
    
    // Also show alert
    setTimeout(() => {
      alert(`✅ ${notification.title}\n\n${notification.message}\n\nEstimated delivery: 30-45 minutes`);
    }, 1000);
  }
}