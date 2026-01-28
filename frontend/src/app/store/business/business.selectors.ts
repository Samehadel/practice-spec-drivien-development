import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AppState } from '../app.state';
import {businessFeature, BusinessState} from './business.reducer';

export const selectBusinessState = createFeatureSelector<BusinessState>('businesses');

export const selectBusinesses = createSelector(selectBusinessState, (state) => state.businesses);
export const selectCurrentBusiness = createSelector(selectBusinessState, (state) => state.currentBusiness);
export const selectBusinessLoading = createSelector(selectBusinessState, (state) => state.loading);
export const selectBusinessError = createSelector(selectBusinessState, (state) => state.error);

export const selectBusinessById = (businessId: string) => createSelector(
  selectBusinesses,
  (businesses) => businesses.find(business => business.id === businessId)
);

export const selectOpenBusinesses = createSelector(
  selectBusinesses,
  (businesses) => businesses.filter(business => business.queueOpen)
);
