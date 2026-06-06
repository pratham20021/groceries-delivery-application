import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService, SignupRequest } from '../../../services/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  signupData: SignupRequest = {
    name: '',
    email: '',
    password: '',
    phone: ''
  };
  
  loading = false;
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}
  
  onSignup() {
    if (!this.signupData.name || !this.signupData.email || !this.signupData.password) {
      alert('Please fill in all required fields');
      return;
    }
    
    if (this.signupData.password.length < 6) {
      alert('Password must be at least 6 characters long');
      return;
    }
    
    this.loading = true;
    
    this.authService.signup(this.signupData).subscribe({
      next: (response) => {
        this.loading = false;
        console.log('Signup successful:', response);
        this.router.navigate(['/home']).then(success => {
          if (!success) {
            console.error('Navigation to /home failed');
            this.router.navigate(['/products']);
          }
        });
      },
      error: (error) => {
        console.error('Signup failed:', error);
        this.loading = false;
        if (error.status === 0) {
          alert('Cannot connect to server. Please ensure backend services are running.');
        } else if (error.error && typeof error.error === 'string') {
          alert(error.error);
        } else {
          alert('Signup failed. Please try again.');
        }
      }
    });
  }
}