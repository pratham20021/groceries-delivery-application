import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterModule } from '@angular/router';
import { AuthService } from './services/auth.service';
import { CartService } from './services/cart.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterModule],
  template: `
    <div class="app-container">
      <nav class="navbar glass-navbar">
        <div class="nav-container">
          <div class="nav-brand">
            <a routerLink="/home" class="brand-link">
              <i class="fas fa-shopping-cart"></i>
              <span class="brand-text">RevCart</span>
            </a>
          </div>
          <div class="nav-menu">
            <a routerLink="/home" class="nav-link" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}">
              <i class="fas fa-home"></i>
              <span>Home</span>
            </a>
            <a routerLink="/products" class="nav-link" routerLinkActive="active">
              <i class="fas fa-th-large"></i>
              <span>Products</span>
            </a>
            <a routerLink="/cart" class="nav-link cart-link" routerLinkActive="active">
              <i class="fas fa-shopping-cart"></i>
              <span>Cart</span>
              <span class="cart-badge" *ngIf="cartItemCount > 0">{{cartItemCount}}</span>
            </a>
            <a routerLink="/orders" class="nav-link" routerLinkActive="active" *ngIf="isLoggedIn">
              <i class="fas fa-box"></i>
              <span>Orders</span>
            </a>
            <a routerLink="/admin" class="nav-link" routerLinkActive="active" *ngIf="isAdmin">
              <i class="fas fa-cog"></i>
              <span>Admin</span>
            </a>
            <a routerLink="/notifications" class="nav-link" routerLinkActive="active" *ngIf="isLoggedIn">
              <i class="fas fa-bell"></i>
              <span>Notifications</span>
            </a>
          </div>
          <div class="nav-auth">
            <div class="auth-buttons" *ngIf="!isLoggedIn">
              <a routerLink="/auth/login" class="btn btn-ghost btn-sm">
                <i class="fas fa-sign-in-alt"></i>
                Login
              </a>
              <a routerLink="/auth/signup" class="btn btn-primary btn-sm">
                <i class="fas fa-user-plus"></i>
                Sign Up
              </a>
            </div>
            <div class="user-menu" *ngIf="isLoggedIn">
              <span class="user-name">
                <i class="fas fa-user-circle"></i>
                {{userName}}
              </span>
              <button class="btn btn-ghost btn-sm" (click)="logout()">
                <i class="fas fa-sign-out-alt"></i>
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: []
})
export class AppComponent implements OnInit {
  title = 'RevCart';
  isLoggedIn = false;
  isAdmin = false;
  userName = '';
  cartItemCount = 0;

  constructor(
    private authService: AuthService,
    private cartService: CartService
  ) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
      if (user) {
        this.userName = user.name;
        this.isAdmin = this.authService.isAdmin();
      }
    });

    this.cartService.getCartItems().pipe(
      map(items => items.reduce((total, item) => total + item.quantity, 0))
    ).subscribe(count => {
      this.cartItemCount = count;
    });
  }

  logout() {
    this.authService.logout();
  }
}
