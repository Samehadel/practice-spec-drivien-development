import { createFeature, createReducer, on } from '@ngrx/store';
import * as UIActions from './ui.actions';

export interface UIState {
  sidebarOpen: boolean;
  loading: boolean;
  notifications: Notification[];
  theme: 'light' | 'dark';
}

export interface Notification {
  id: string;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
  timestamp: number;
  autoClose?: boolean;
}

const initialState: UIState = {
  sidebarOpen: true,
  loading: false,
  notifications: [],
  theme: 'light'
};

export const uiFeature = createFeature({
  name: 'ui',
  reducer: createReducer(
    initialState,
    on(UIActions.toggleSidebar, (state) => ({
      ...state,
      sidebarOpen: !state.sidebarOpen
    })),
    on(UIActions.setLoading, (state, { loading }) => ({
      ...state,
      loading
    })),
    on(UIActions.showNotification, (state, { notification }) => ({
      ...state,
      notifications: [...state.notifications, { ...notification, id: Date.now().toString(), timestamp: Date.now() }]
    })),
    on(UIActions.removeNotification, (state, { notificationId }) => ({
      ...state,
      notifications: state.notifications.filter(n => n.id !== notificationId)
    })),
    on(UIActions.clearNotifications, (state) => ({
      ...state,
      notifications: []
    })),
    on(UIActions.setTheme, (state, { theme }) => ({
      ...state,
      theme
    }))
  )
});
