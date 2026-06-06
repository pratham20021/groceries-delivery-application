export interface Coupon {
  id?: number;
  code: string;
  description: string;
  discountType: 'PERCENTAGE' | 'FIXED';
  discountValue: number;
  minOrderAmount?: number;
  maxDiscountAmount?: number;
  validFrom?: string;
  validUntil?: string;
  usageLimit?: number;
  usedCount?: number;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
}
