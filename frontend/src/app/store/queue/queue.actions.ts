import { createAction, props } from '@ngrx/store';
import { QueueEntry } from './queue.reducer';

export const loadQueue = createAction('[Queue] Load Queue', props<{ businessId: string }>());
export const loadQueueSuccess = createAction('[Queue] Load Queue Success', props<{ queue: QueueEntry[] }>());
export const loadQueueFailure = createAction('[Queue] Load Queue Failure', props<{ error: string }>());

export const advanceQueue = createAction('[Queue] Advance Queue', props<{ businessId: string; action: 'serve' | 'no-show'; notes?: string }>());
export const advanceQueueSuccess = createAction('[Queue] Advance Queue Success', props<{ queue: QueueEntry[] }>());
export const advanceQueueFailure = createAction('[Queue] Advance Queue Failure', props<{ error: string }>());

export const skipCustomer = createAction('[Queue] Skip Customer', props<{ businessId: string; queueEntryId: string; reason?: string }>());
export const skipCustomerSuccess = createAction('[Queue] Skip Customer Success', props<{ queue: QueueEntry[] }>());
export const skipCustomerFailure = createAction('[Queue] Skip Customer Failure', props<{ error: string }>());

export const markServed = createAction('[Queue] Mark Served', props<{ businessId: string; queueEntryId: string }>());
export const markServedSuccess = createAction('[Queue] Mark Served Success', props<{ queue: QueueEntry[] }>());
export const markServedFailure = createAction('[Queue] Mark Served Failure', props<{ error: string }>());

export const markNoShow = createAction('[Queue] Mark No-Show', props<{ businessId: string; queueEntryId: string }>());
export const markNoShowSuccess = createAction('[Queue] Mark No-Show Success', props<{ queue: QueueEntry[] }>());
export const markNoShowFailure = createAction('[Queue] Mark No-Show Failure', props<{ error: string }>());

export const updateQueueStatus = createAction('[Queue] Update Queue Status', props<{ businessId: string; queueOpen: boolean }>());
export const updateQueueStatusSuccess = createAction('[Queue] Update Queue Status Success', props<{ queueOpen: boolean }>());
export const updateQueueStatusFailure = createAction('[Queue] Update Queue Status Failure', props<{ error: string }>());

export const clearQueueError = createAction('[Queue] Clear Error');
