import { createAction, props } from '@ngrx/store';
import { Notification } from './ui.reducer';

export const toggleSidebar = createAction('[UI] Toggle Sidebar');
export const setLoading = createAction('[UI] Set Loading', props<{ loading: boolean }>());
export const showNotification = createAction('[UI] Show Notification', props<{ notification: Omit<Notification, 'id' | 'timestamp'> }>());
export const removeNotification = createAction('[UI] Remove Notification', props<{ notificationId: string }>());
export const clearNotifications = createAction('[UI] Clear Notifications');
export const setTheme = createAction('[UI] Set Theme', props<{ theme: 'light' | 'dark' }>());
