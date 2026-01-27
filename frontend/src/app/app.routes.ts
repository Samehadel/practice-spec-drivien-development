import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { QueueManagementComponent } from './pages/queue-management/queue-management.component';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'queue', component: QueueManagementComponent },
  { path: '**', redirectTo: '/dashboard' }
];
