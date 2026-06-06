import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An error occurred';
      
      if (error.error instanceof ErrorEvent) {
        errorMessage = `Error: ${error.error.message}`;
      } else {
        switch (error.status) {
          case 401:
            errorMessage = 'Unauthorized. Please login again.';
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            router.navigate(['/auth/login']);
            break;
          case 403:
            errorMessage = 'Access forbidden.';
            break;
          case 404:
            errorMessage = 'Resource not found.';
            break;
          case 500:
            errorMessage = 'Server error. Please try again later.';
            break;
          case 0:
            errorMessage = 'Cannot connect to server. Please check your connection.';
            break;
          default:
            errorMessage = error.error?.message || `Error: ${error.status}`;
        }
      }
      
      console.error('HTTP Error:', errorMessage, error);
      return throwError(() => ({ ...error, userMessage: errorMessage }));
    })
  );
};
