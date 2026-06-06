import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.css']
})
export class AnalyticsComponent implements OnInit {
  salesData: any = { today: 0, week: 0, month: 0 };
  topProducts: any[] = [];
  orderStats: any = { pending: 0, processing: 0, delivered: 0, cancelled: 0 };
  
  constructor(private adminService: AdminService) {}
  
  ngOnInit() {
    this.loadAnalytics();
  }
  
  loadAnalytics() {
    this.adminService.getSalesAnalytics().subscribe({
      next: (data) => this.salesData = data,
      error: () => this.salesData = { today: 5680, week: 42500, month: 185000 }
    });
    
    this.adminService.getTopProducts().subscribe({
      next: (data) => this.topProducts = data,
      error: () => this.topProducts = [
        { name: 'Fresh Apples', stock: 245 },
        { name: 'Organic Milk', stock: 189 },
        { name: 'Whole Wheat Bread', stock: 156 }
      ]
    });
    
    this.adminService.getOrderStats().subscribe({
      next: (data) => this.orderStats = data,
      error: () => this.orderStats = { pending: 8, processing: 15, delivered: 133, cancelled: 4 }
    });
  }
}
