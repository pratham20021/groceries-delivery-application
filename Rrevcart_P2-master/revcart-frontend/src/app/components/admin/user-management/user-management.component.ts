import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: any[] = [];
  loading = false;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.http.get<any[]>(`${environment.apiUrl}/auth/users`).subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading users:', error);
        alert('Failed to load users');
        this.loading = false;
      }
    });
  }

  updateRole(userId: number, newRole: string) {
    if (!confirm(`Are you sure you want to change this user's role to ${newRole}?`)) {
      return;
    }

    this.http.put(`${environment.apiUrl}/auth/users/${userId}/role`, { role: newRole }).subscribe({
      next: () => {
        alert('User role updated successfully');
        this.loadUsers();
      },
      error: (error) => {
        console.error('Error updating role:', error);
        alert('Failed to update user role');
      }
    });
  }
}
