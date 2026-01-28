import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { BusinessActions } from './business-action-types';
import { BusinessService } from '../../services/api/business.service';

@Injectable()
export class BusinessEffects {
  
  loadBusinesses$ = createEffect(() =>
    this.actions$.pipe(
      ofType(BusinessActions.loadBusinesses),
      mergeMap(() =>
        this.businessService.getBusinesses().pipe(
          map(response => BusinessActions.loadBusinessesSuccess({ businesses: response.data })),
          catchError(error => of(BusinessActions.loadBusinessesFailure({ error: error.message })))
        )
      )
    )
  );

  loadBusiness$ = createEffect(() =>
    this.actions$.pipe(
      ofType(BusinessActions.loadBusiness),
      mergeMap(({ businessId }) =>
        this.businessService.getBusiness(businessId).pipe(
          map(business => BusinessActions.loadBusinessSuccess({ business })),
          catchError(error => of(BusinessActions.loadBusinessFailure({ error: error.message })))
        )
      )
    )
  );

  createBusiness$ = createEffect(() =>
    this.actions$.pipe(
      ofType(BusinessActions.createBusiness),
      mergeMap(({ business }) =>
        this.businessService.createBusiness(business).pipe(
          map(createdBusiness => BusinessActions.createBusinessSuccess({ business: createdBusiness })),
          catchError(error => of(BusinessActions.createBusinessFailure({ error: error.message })))
        )
      )
    )
  );

  updateBusiness$ = createEffect(() =>
    this.actions$.pipe(
      ofType(BusinessActions.updateBusiness),
      mergeMap(({ businessId, business }) =>
        this.businessService.updateBusiness(businessId, business).pipe(
          map(updatedBusiness => BusinessActions.updateBusinessSuccess({ business: updatedBusiness })),
          catchError(error => of(BusinessActions.updateBusinessFailure({ error: error.message })))
        )
      )
    )
  );

  constructor(
    private actions$: Actions,
    private businessService: BusinessService
  ) {}
}
