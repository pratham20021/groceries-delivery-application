import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CouponService } from '../../../services/coupon.service';
import { Coupon } from '../../../models/coupon.model';

@Component({
  selector: 'app-admin-coupons',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-coupons.component.html',
  styleUrls: ['./admin-coupons.component.css']
})
export class AdminCouponsComponent implements OnInit {
  coupons: Coupon[] = [];
  showForm = false;
  editMode = false;
  currentCoupon: Coupon = this.getEmptyCoupon();

  constructor(private couponService: CouponService) {}

  ngOnInit() {
    this.loadCoupons();
  }

  loadCoupons() {
    console.log('Loading coupons...');
    
    // Load from localStorage
    const storedCoupons = JSON.parse(localStorage.getItem('coupons') || '[]');
    
    if (storedCoupons.length > 0) {
      this.coupons = storedCoupons;
    } else {
      // Default coupons
      this.coupons = [
        {
          id: 1,
          code: 'SAVE10',
          description: '10% off on orders above ₹500',
          discountType: 'PERCENTAGE',
          discountValue: 10,
          active: true
        },
        {
          id: 2,
          code: 'FLAT50',
          description: '₹50 off on all orders',
          discountType: 'FIXED',
          discountValue: 50,
          active: true
        }
      ];
      localStorage.setItem('coupons', JSON.stringify(this.coupons));
    }
  }

  getEmptyCoupon(): Coupon {
    return {
      code: '',
      description: '',
      discountType: 'PERCENTAGE',
      discountValue: 0,
      active: true
    };
  }

  openCreateForm() {
    this.editMode = false;
    this.currentCoupon = this.getEmptyCoupon();
    this.showForm = true;
  }

  openEditForm(coupon: Coupon) {
    this.editMode = true;
    this.currentCoupon = { ...coupon };
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.currentCoupon = this.getEmptyCoupon();
  }

  saveCoupon() {
    // Save to localStorage for now
    const coupons = JSON.parse(localStorage.getItem('coupons') || '[]');
    
    if (this.editMode && this.currentCoupon.id) {
      const index = coupons.findIndex((c: any) => c.id === this.currentCoupon.id);
      if (index >= 0) {
        coupons[index] = this.currentCoupon;
      }
    } else {
      this.currentCoupon.id = Date.now();
      coupons.push(this.currentCoupon);
    }
    
    localStorage.setItem('coupons', JSON.stringify(coupons));
    this.loadCoupons();
    this.closeForm();
    alert('Coupon saved successfully');
  }

  deleteCoupon(id: number) {
    if (confirm('Are you sure you want to delete this coupon?')) {
      this.couponService.deleteCoupon(id).subscribe({
        next: () => {
          this.loadCoupons();
          alert('Coupon deleted successfully');
        },
        error: (err) => alert('Error deleting coupon: ' + (err.error?.message || err.message || 'Unknown error'))
      });
    }
  }

  toggleStatus(coupon: Coupon) {
    coupon.active = !coupon.active;
    if (coupon.id) {
      this.couponService.updateCoupon(coupon.id, coupon).subscribe({
        next: () => alert('Coupon status updated'),
        error: (err) => alert('Error updating status: ' + (err.error?.message || err.message || 'Unknown error'))
      });
    }
  }
}
