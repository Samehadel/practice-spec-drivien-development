import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import {Observable} from "rxjs";


export interface WhatsAppWebhookResponse {
  status: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class WhatsAppService extends BaseApiService {

  private readonly baseUrl = '/webhooks/whatsapp';

  verifyWebhook(): Observable<string> {
    return this.get<string>(this.baseUrl);
  }

  handleWebhook(payload: WhatsAppWebhookRequest): Observable<WhatsAppWebhookResponse> {
    return this.post<WhatsAppWebhookResponse>(this.baseUrl, payload);
  }
}

export interface WhatsAppWebhookRequest {
  object: string;
  entry: Array<{
    id: string;
    changes: Array<{
      field: string;
      value: {
        messaging_product: string;
        metadata: {
          display_phone_number: string;
          phone_number_id: string;
        };
        contacts?: Array<{
          profile: {
            name: string;
          };
          wa_id: string;
        }>;
        messages?: Array<{
          from: string;
          id: string;
          timestamp: string;
          text?: {
            body: string;
          };
          type: string;
        }>;
      }>;
  }>;
}