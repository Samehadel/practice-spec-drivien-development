import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AppState } from '../app.state';
import {uiFeature, UIState} from './ui.reducer';

export const selectUIState = createFeatureSelector<UIState>('ui');

export const selectSidebarOpen = createSelector(selectUIState, (state) => state.sidebarOpen);
export const selectLoading = createSelector(selectUIState, (state) => state.loading);
export const selectNotifications = createSelector(selectUIState, (state) => state.notifications);
export const selectTheme = createSelector(selectUIState, (state) => state.theme);

export const selectUnreadNotifications = createSelector(
  selectNotifications,
  (notifications) => notifications.length
);
