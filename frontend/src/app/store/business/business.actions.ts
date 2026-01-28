import { createAction, props } from '@ngrx/store';
import { Business } from './business.reducer';

export const loadBusinesses = createAction('[Business] Load Businesses');
export const loadBusinessesSuccess = createAction('[Business] Load Businesses Success', props<{ businesses: Business[] }>());
export const loadBusinessesFailure = createAction('[Business] Load Businesses Failure', props<{ error: string }>());

export const loadBusiness = createAction('[Business] Load Business', props<{ businessId: string }>());
export const loadBusinessSuccess = createAction('[Business] Load Business Success', props<{ business: Business }>());
export const loadBusinessFailure = createAction('[Business] Load Business Failure', props<{ error: string }>());

export const createBusiness = createAction('[Business] Create Business', props<{ business: Omit<Business, 'id' | 'createdAt' | 'updatedAt'> }>());
export const createBusinessSuccess = createAction('[Business] Create Business Success', props<{ business: Business }>());
export const createBusinessFailure = createAction('[Business] Create Business Failure', props<{ error: string }>());

export const updateBusiness = createAction('[Business] Update Business', props<{ businessId: string; business: Partial<Business> }>());
export const updateBusinessSuccess = createAction('[Business] Update Business Success', props<{ business: Business }>());
export const updateBusinessFailure = createAction('[Business] Update Business Failure', props<{ error: string }>());

export const clearBusinessError = createAction('[Business] Clear Error');
