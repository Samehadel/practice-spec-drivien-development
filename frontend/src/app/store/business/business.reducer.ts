import { createFeature, createReducer, on } from '@ngrx/store';
import { BusinessActions } from './business-action-types';

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

export interface BusinessState {
  businesses: Business[];
  currentBusiness: Business | null;
  loading: boolean;
  error: string | null;
}

const initialState: BusinessState = {
  businesses: [],
  currentBusiness: null,
  loading: false,
  error: null
};

export const businessFeature = createFeature({
  name: 'business',
  reducer: createReducer(
    initialState,
    on(BusinessActions.loadBusinesses, (state) => ({
      ...state,
      loading: true,
      error: null
    })),
    on(BusinessActions.loadBusinessesSuccess, (state, { businesses }) => ({
      ...state,
      businesses,
      loading: false,
      error: null
    })),
    on(BusinessActions.loadBusinessesFailure, (state, { error }) => ({
      ...state,
      loading: false,
      error
    })),
    on(BusinessActions.loadBusiness, (state) => ({
      ...state,
      loading: true,
      error: null
    })),
    on(BusinessActions.loadBusinessSuccess, (state, { business }) => ({
      ...state,
      currentBusiness: business,
      loading: false,
      error: null
    })),
    on(BusinessActions.loadBusinessFailure, (state, { error }) => ({
      ...state,
      loading: false,
      error
    })),
    on(BusinessActions.createBusiness, (state) => ({
      ...state,
      loading: true,
      error: null
    })),
    on(BusinessActions.createBusinessSuccess, (state, { business }) => ({
      ...state,
      businesses: [...state.businesses, business],
      loading: false,
      error: null
    })),
    on(BusinessActions.createBusinessFailure, (state, { error }) => ({
      ...state,
      loading: false,
      error
    })),
    on(BusinessActions.updateBusiness, (state) => ({
      ...state,
      loading: true,
      error: null
    })),
    on(BusinessActions.updateBusinessSuccess, (state, { business }) => ({
      ...state,
      businesses: state.businesses.map(b => b.id === business.id ? business : b),
      currentBusiness: state.currentBusiness?.id === business.id ? business : state.currentBusiness,
      loading: false,
      error: null
    })),
    on(BusinessActions.updateBusinessFailure, (state, { error }) => ({
      ...state,
      loading: false,
      error
    }))
  )
});
