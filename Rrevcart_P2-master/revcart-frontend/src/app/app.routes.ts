import { Routes } from '@angular/router';
import { AdminGuard } from './guards/admin.guard';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  
  // Home
  { path: 'home', loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent) },
  
  // Auth Routes
  { path: 'auth/login', loadComponent: () => import('./components/auth/login/login.component').then(m => m.LoginComponent) },
  { path: 'auth/signup', loadComponent: () => import('./components/auth/signup/signup.component').then(m => m.SignupComponent) },
  { path: 'oauth2/redirect', loadComponent: () => import('./components/auth/oauth2-redirect/oauth2-redirect.component').then(m => m.OAuth2RedirectComponent) },
  { path: 'forgot-password', loadComponent: () => import('./components/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent) },
  
  // Products
  { path: 'products', loadComponent: () => import('./components/products/product-list/product-list.component').then(m => m.ProductListComponent) },
  { path: 'products/:id', loadComponent: () => import('./components/products/product-detail/product-detail.component').then(m => m.ProductDetailComponent) },
  
  // Cart & Checkout
  { path: 'cart', loadComponent: () => import('./components/cart/cart.component').then(m => m.CartComponent), canActivate: [AuthGuard] },
  { path: 'checkout', loadComponent: () => import('./components/checkout/checkout.component').then(m => m.CheckoutComponent), canActivate: [AuthGuard] },
  { path: 'payment', loadComponent: () => import('./components/payment/payment.component').then(m => m.PaymentComponent), canActivate: [AuthGuard] },
  { path: 'order-success', loadComponent: () => import('./components/order-success/order-success.component').then(m => m.OrderSuccessComponent), canActivate: [AuthGuard] },
  
  // Orders
  { path: 'orders', loadComponent: () => import('./components/orders/order-list/order-list.component').then(m => m.OrderListComponent) },
  { path: 'orders/:id', loadComponent: () => import('./components/orders/track-order/track-order.component').then(m => m.TrackOrderComponent), canActivate: [AuthGuard] },
  
  // Debug
  { path: 'debug-orders', loadComponent: () => import('./components/debug-orders.component').then(m => m.DebugOrdersComponent) },
  
  // Admin Routes (Protected)
  { 
    path: 'admin', 
    canActivate: [AdminGuard],
    children: [
      { path: '', loadComponent: () => import('./components/admin/dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent) },
      { path: 'products', loadComponent: () => import('./components/admin/products/admin-products.component').then(m => m.AdminProductsComponent) },
      { path: 'products/add', loadComponent: () => import('./components/admin/add-product/add-product.component').then(m => m.AddProductComponent) },
      { path: 'products/edit/:id', loadComponent: () => import('./components/admin/edit-product/edit-product.component').then(m => m.EditProductComponent) },
      { path: 'orders', loadComponent: () => import('./components/admin/orders/admin-orders.component').then(m => m.AdminOrdersComponent) },
      { path: 'users', loadComponent: () => import('./components/admin/users/admin-users.component').then(m => m.AdminUsersComponent) },
      { path: 'user-management', loadComponent: () => import('./components/admin/user-management/user-management.component').then(m => m.UserManagementComponent) },
      { path: 'coupons', loadComponent: () => import('./components/admin/coupons/admin-coupons.component').then(m => m.AdminCouponsComponent) },
      { path: 'delivery-agents', loadComponent: () => import('./components/admin/delivery-agents/delivery-agents.component').then(m => m.DeliveryAgentsComponent) },
      { path: 'analytics', loadComponent: () => import('./components/admin/analytics/analytics.component').then(m => m.AnalyticsComponent) }
    ]
  },
  
  // Notifications
  { path: 'notifications', loadComponent: () => import('./components/notifications-page/notifications-page.component').then(m => m.NotificationsPageComponent), canActivate: [AuthGuard] },
  
  // Delivery
  { path: 'delivery', loadComponent: () => import('./components/delivery/delivery-dashboard.component').then(m => m.DeliveryDashboardComponent), canActivate: [AuthGuard] },
  
  // Fallback
  { path: '**', redirectTo: '/home' }
];
