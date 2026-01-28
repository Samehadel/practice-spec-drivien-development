import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { QueueActions } from './queue-action-type';
import { QueueService } from '../../services/api/queue.service';

@Injectable()
export class QueueEffects {
  
  loadQueue$ = createEffect(() =>
    this.actions$.pipe(
      ofType(QueueActions.loadQueue),
      mergeMap(({ businessId }) =>
        this.queueService.getBusinessQueue(businessId).pipe(
          map(response => QueueActions.loadQueueSuccess({ queue: response.queue.activeEntries })),
          catchError(error => of(QueueActions.loadQueueFailure({ error: error.message })))
        )
      )
    )
  );

  advanceQueue$ = createEffect(() =>
    this.actions$.pipe(
      ofType(QueueActions.advanceQueue),
      mergeMap(({ businessId, action, notes }) =>
        this.queueService.advanceQueue(businessId, { action, notes }).pipe(
          map(response => QueueActions.advanceQueueSuccess({ queue: response.queue })),
          catchError(error => of(QueueActions.advanceQueueFailure({ error: error.message })))
        )
      )
    )
  );

  skipCustomer$ = createEffect(() =>
    this.actions$.pipe(
      ofType(QueueActions.skipCustomer),
      mergeMap(({ businessId, queueEntryId, reason }) =>
        this.queueService.skipCustomer(businessId, { queueEntryId, reason }).pipe(
          map(response => QueueActions.skipCustomerSuccess({ queue: response.queue })),
          catchError(error => of(QueueActions.skipCustomerFailure({ error: error.message })))
        )
      )
    )
  );

  markServed$ = createEffect(() =>
    this.actions$.pipe(
      ofType(QueueActions.markServed),
      mergeMap(({ businessId, queueEntryId }) =>
        this.queueService.markServed(businessId, queueEntryId).pipe(
          map(response => QueueActions.markServedSuccess({ queue: response.queue })),
          catchError(error => of(QueueActions.markServedFailure({ error: error.message })))
        )
      )
    )
  );

  markNoShow$ = createEffect(() =>
    this.actions$.pipe(
      ofType(QueueActions.markNoShow),
      mergeMap(({ businessId, queueEntryId }) =>
        this.queueService.markNoShow(businessId, queueEntryId).pipe(
          map(response => QueueActions.markNoShowSuccess({ queue: response.queue })),
          catchError(error => of(QueueActions.markNoShowFailure({ error: error.message })))
        )
      )
    )
  );

  updateQueueStatus$ = createEffect(() =>
    this.actions$.pipe(
      ofType(QueueActions.updateQueueStatus),
      mergeMap(({ businessId, queueOpen }) =>
        this.queueService.updateQueueStatus(businessId, { queueOpen }).pipe(
          map(() => QueueActions.updateQueueStatusSuccess({ queueOpen })),
          catchError(error => of(QueueActions.updateQueueStatusFailure({ error: error.message })))
        )
      )
    )
  );

  constructor(
    private actions$: Actions,
    private queueService: QueueService
  ) {}
}
