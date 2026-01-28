import { Injectable } from '@angular/core';
import { BaseApiService, ApiResponse } from './base-api.service';
import {Observable} from "rxjs";

export interface Business {
  id: string;
  name: string;
  serviceType: string;
  whatsappPhoneNumber: string;
  queueOpen: boolean;
  averageServiceTimeMinutes: number;
  notificationThreshold: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateBusinessRequest {
  name: string;
  serviceType: string;
  whatsappPhoneNumber: string;
  averageServiceTimeMinutes?: number;
  notificationThreshold?: number;
}

export interface UpdateBusinessRequest {
  name?: string;
  serviceType?: string;
  averageServiceTimeMinutes?: number;
  notificationThreshold?: number;
}

@Injectable({
  providedIn: 'root'
})
export class BusinessService extends BaseApiService {

  private readonly baseUrl = '/v1/businesses';

  getBusinesses(): Observable<ApiResponse<Business[]>> {
    return this.get<ApiResponse<Business[]>>(this.baseUrl);
  }

  getBusiness(businessId: string): Observable<Business> {
    return this.get<Business>(`${this.baseUrl}/${businessId}`);
  }

  createBusiness(business: CreateBusinessRequest): Observable<Business> {
    return this.post<Business>(this.baseUrl, business);
  }

  updateBusiness(businessId: string, business: UpdateBusinessRequest): Observable<Business> {
    return this.put<Business>(`${this.baseUrl}/${businessId}`, business);
  }
}
