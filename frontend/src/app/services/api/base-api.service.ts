import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

export interface ApiResponse<T> {
  data: T;
  pagination?: {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details?: any[];
}

@Injectable({
  providedIn: 'root'
})
export class BaseApiService {

  constructor(protected http: HttpClient) {}

  protected get<T>(url: string): Observable<T> {
    return this.http.get<T>(url).pipe(
      catchError(this.handleError)
    );
  }

  protected post<T>(url: string, body: any): Observable<T> {
    return this.http.post<T>(url, body).pipe(
      catchError(this.handleError)
    );
  }

  protected put<T>(url: string, body: any): Observable<T> {
    return this.http.put<T>(url, body).pipe(
      catchError(this.handleError)
    );
  }

  protected delete<T>(url: string): Observable<T> {
    return this.http.delete<T>(url).pipe(
      catchError(this.handleError)
    );
  }

  protected patch<T>(url: string, body: any): Observable<T> {
    return this.http.patch<T>(url, body).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An unknown error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      if (error.error && error.error.message) {
        errorMessage = error.error.message;
      } else {
        errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
      }
    }
    
    return throwError(() => errorMessage);
  }
}
