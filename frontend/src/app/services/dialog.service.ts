import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';

export interface ConfirmDialogData {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
}

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(private dialog: MatDialog) {}

  confirm(data: ConfirmDialogData): Observable<boolean> {
    // Placeholder for confirmation dialog
    // Will be implemented with actual dialog component
    return new Observable(observer => {
      const result = window.confirm(`${data.title}\n\n${data.message}`);
      observer.next(result);
      observer.complete();
    });
  }

  alert(title: string, message: string): Observable<void> {
    return new Observable(observer => {
      window.alert(`${title}\n\n${message}`);
      observer.next();
      observer.complete();
    });
  }
}
