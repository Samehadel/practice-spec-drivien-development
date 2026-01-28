import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AppState } from '../app.state';
import {queueFeature, QueueState} from './queue.reducer';

export const selectQueueState = createFeatureSelector<QueueState>('queue');

export const selectQueueEntries = createSelector(selectQueueState, (state) => state.entries);
export const selectQueueLoading = createSelector(selectQueueState, (state) => state.loading);
export const selectQueueError = createSelector(selectQueueState, (state) => state.error);
export const selectCurrentPosition = createSelector(selectQueueState, (state) => state.currentPosition);
export const selectEstimatedWaitTime = createSelector(selectQueueState, (state) => state.estimatedWaitTime);

export const selectActiveQueueEntries = createSelector(selectQueueEntries, (entries) =>
  entries.filter(entry => entry.status === 'ACTIVE')
);

export const selectCurrentCustomer = createSelector(selectActiveQueueEntries, (entries) =>
  entries.length > 0 ? entries[0] : null
);

export const selectQueueSize = createSelector(selectActiveQueueEntries, (entries) => entries.length);

export const selectQueueByBusinessId = (businessId: string) => createSelector(
  selectQueueEntries,
  (entries) => entries.filter(entry => entry.businessId === businessId)
);
