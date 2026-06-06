import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { ProductService, Product } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { CategorySliderComponent } from '../shared/category-slider/category-slider.component';
import { ProductCardComponent } from '../shared/product-card/product-card.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, CategorySliderComponent, ProductCardComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  trendingProducts: Product[] = [];
  loading = false;
  currentPage = 0;
  pageSize = 8;
  hasMoreProducts = true;

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (params['token']) {
        const user = {
          id: +params['id'],
          name: params['name'],
          email: params['email'],
          role: params['role']
        };
        localStorage.setItem('token', params['token']);
        localStorage.setItem('user', JSON.stringify(user));
        window.history.replaceState({}, '', '/home');
      }
    });
    this.loadProducts();
  }

  loadProducts() {
    if (this.loading || !this.hasMoreProducts) return;
    
    this.loading = true;
    this.productService.getProducts(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        const newProducts = response.content || [];
        this.trendingProducts = [...this.trendingProducts, ...newProducts];
        this.hasMoreProducts = !response.last;
        this.currentPage++;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading products:', error);
        console.log('API URL:', `${this.productService['apiUrl']}?page=${this.currentPage}&size=${this.pageSize}`);
        this.loading = false;
        this.hasMoreProducts = false;
      }
    });
  }



  @HostListener('window:scroll', ['$event'])
  onScroll() {
    const threshold = 200;
    const position = window.pageYOffset + window.innerHeight;
    const height = document.documentElement.scrollHeight;
    
    if (position > height - threshold) {
      this.loadProducts();
    }
  }

  trackByProductId(index: number, product: Product): number {
    return product.id;
  }
}