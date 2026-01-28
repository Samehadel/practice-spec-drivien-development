import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import {Observable} from "rxjs";

export interface QueueEntry {
  id: string;
  businessId: string;
  whatsappIdentifier: string;
  customerName?: string;
  status: 'ACTIVE' | 'SERVED' | 'NO_SHOW' | 'LEFT';
  joinedAt: string;
  servedAt?: string;
  position: number;
  estimatedWaitTimeMinutes: number;
}

export interface BusinessQueue {
  business: any;
  queue: {
    isOpen: boolean;
    activeEntries: QueueEntry[];
    estimatedWaitTime: number;
  };
}

export interface AdvanceQueueRequest {
  action: 'serve' | 'no-show';
  notes?: string;
}

export interface SkipCustomerRequest {
  queueEntryId: string;
  reason?: string;
  notes?: string;
}

export interface UpdateQueueStatusRequest {
  queueOpen: boolean;
  reason?: string;
}

@Injectable({
  providedIn: 'root'
})
export class QueueService extends BaseApiService {

  private readonly baseUrl = '/v1/queue';

  getBusinessQueue(businessId: string): Observable<BusinessQueue> {
    return this.get<BusinessQueue>(`${this.baseUrl}/${businessId}`);
  }

  advanceQueue(businessId: string, request: AdvanceQueueRequest): Observable<BusinessQueue> {
    return this.post<BusinessQueue>(`${this.baseUrl}/${businessId}/advance`, request);
  }

  skipCustomer(businessId: string, request: SkipCustomerRequest): Observable<BusinessQueue> {
    return this.post<BusinessQueue>(`${this.baseUrl}/${businessId}/skip`, request);
  }

  markServed(businessId: string, queueEntryId: string): Observable<BusinessQueue> {
    return this.post<BusinessQueue>(`${this.baseUrl}/${businessId}/serve`, { queueEntryId });
  }

  markNoShow(businessId: string, queueEntryId: string): Observable<BusinessQueue> {
    return this.post<BusinessQueue>(`${this.baseUrl}/${businessId}/no-show`, { queueEntryId });
  }

  updateQueueStatus(businessId: string, request: UpdateQueueStatusRequest): Observable<any> {
    return this.put(`${this.baseUrl}/${businessId}/status`, request);
  }
}
