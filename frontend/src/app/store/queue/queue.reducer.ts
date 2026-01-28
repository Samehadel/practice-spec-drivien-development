import { createFeature, createReducer, on } from '@ngrx/store';
import { QueueActions } from './queue-action-type';

export interface QueueEntry {
  id: string;
  businessId: string;
  whatsappIdentifier: string;
  customerName?: string;
  status: 'ACTIVE' | 'SERVED' | 'NO_SHOW' | 'LEFT';
  joinedAt: string;
  servedAt?: string;
  position: number;
  estimatedWaitTimeMinutes: number;
}

export interface QueueState {
  entries: QueueEntry[];
  currentPosition: number;
  estimatedWaitTime: number;
  loading: boolean;
  error: string | null;
}

const initialState: QueueState = {
  entries: [],
  currentPosition: 0,
  estimatedWaitTime: 0,
  loading: false,
  error: null
};

export const queueFeature = createFeature({
  name: 'queue',
  reducer: createReducer(
    initialState,
    on(QueueActions.loadQueue, (state) => ({
      ...state,
      loading: true,
      error: null
    })),
    on(QueueActions.loadQueueSuccess, (state, { queue }) => ({
      ...state,
      entries: queue,
      loading: false,
      error: null
    })),
    on(QueueActions.loadQueueFailure, (state, { error }) => ({
      ...state,
      loading: false,
      error
    })),
    on(QueueActions.advanceQueue, (state) => ({
      ...state,
      loading: true,
      error: null
    })),
    on(QueueActions.advanceQueueSuccess, (state, { queue }) => ({
      ...state,
      entries: queue,
      loading: false,
      error: null
    })),
    on(QueueActions.advanceQueueFailure, (state, { error }) => ({
      ...state,
      loading: false,
      error
    })),
    on(QueueActions.skipCustomer, (state) => ({
      ...state,
      loading: true,
      error: null
    })),
    on(QueueActions.skipCustomerSuccess, (state, { queue }) => ({
      ...state,
      entries: queue,
      loading: false,
      error: null
    })),
    on(QueueActions.skipCustomerFailure, (state, { error }) => ({
      ...state,
      loading: false,
      error
    }))
  )
});
