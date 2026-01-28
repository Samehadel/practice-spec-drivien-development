import { queueFeature } from './queue/queue.reducer';
import { businessFeature } from './business/business.reducer';
import { uiFeature } from './ui/ui.reducer';

export interface AppState {
  queue: ReturnType<typeof queueFeature>;
  business: ReturnType<typeof businessFeature>;
  ui: ReturnType<typeof uiFeature>;
}
