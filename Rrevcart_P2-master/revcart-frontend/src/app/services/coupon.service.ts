import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Coupon } from '../models/coupon.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CouponService {
  private apiUrl = 'http://localhost:8090/admin/coupons';
  private orderApiUrl = `${environment.orderService}/coupons`;

  constructor(private http: HttpClient) {}

  getAllCoupons(): Observable<Coupon[]> {
    return this.http.get<Coupon[]>(this.apiUrl);
  }

  getCouponById(id: number): Observable<Coupon> {
    return this.http.get<Coupon>(`${this.apiUrl}/${id}`);
  }

  createCoupon(coupon: Coupon): Observable<Coupon> {
    return this.http.post<Coupon>(this.apiUrl, coupon);
  }

  updateCoupon(id: number, coupon: Coupon): Observable<Coupon> {
    return this.http.put<Coupon>(`${this.apiUrl}/${id}`, coupon);
  }

  deleteCoupon(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  validateCoupon(code: string, orderAmount: number): Observable<any> {
    return new Observable(observer => {
      const coupons = JSON.parse(localStorage.getItem('coupons') || '[]');
      const coupon = coupons.find((c: any) => c.code === code && c.active);
      
      if (coupon) {
        let discount = 0;
        if (coupon.discountType === 'PERCENTAGE') {
          discount = orderAmount * (coupon.discountValue / 100);
        } else {
          discount = coupon.discountValue;
        }
        
        observer.next({
          valid: true,
          discount: discount,
          message: `Coupon applied: ${coupon.description}`
        });
      } else {
        observer.next({
          valid: false,
          discount: 0,
          message: 'Invalid coupon code'
        });
      }
      observer.complete();
    });
  }
}
