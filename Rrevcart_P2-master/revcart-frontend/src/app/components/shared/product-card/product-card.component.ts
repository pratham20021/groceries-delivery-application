import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { Product } from '../../../services/product.service';
import { CartService } from '../../../services/cart.service';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css']
})
export class ProductCardComponent implements OnInit, OnDestroy {
  @Input() product!: Product;
  
  quantity = 0;
  private cartSubscription?: Subscription;

  constructor(private cartService: CartService) {}

  ngOnInit() {
    this.cartSubscription = this.cartService.getCartItems().subscribe(cartItems => {
      const cartItem = cartItems.find(item => item.id === this.product.id);
      this.quantity = cartItem ? cartItem.quantity : 0;
    });
  }

  ngOnDestroy() {
    if (this.cartSubscription) {
      this.cartSubscription.unsubscribe();
    }
  }

  addToCart() {
    const result = this.cartService.addToCart(this.product);
    if (!result.success && result.message) {
      alert(result.message);
    }
  }

  increaseQuantity() {
    const result = this.cartService.increaseQuantity(this.product.id);
    if (!result.success && result.message) {
      alert(result.message);
    }
  }

  decreaseQuantity() {
    this.cartService.decreaseQuantity(this.product.id);
  }

  getStars(rating?: number): number[] {
    if (!rating) return Array(4).fill(0); // Default 4 stars if no rating
    return Array(Math.floor(rating)).fill(0);
  }

  getCurrentStock(): number {
    return this.product.currentStock || this.product.stock || 0;
  }

  isInStock(): boolean {
    return this.getCurrentStock() > 0;
  }

  getRating(): number {
    return this.product.rating || 4.0; // Default rating
  }
}