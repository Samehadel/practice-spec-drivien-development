import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient } from '@angular/common/http';
import { provideStore } from '@ngrx/store';
import { provideStoreDevtools } from '@ngrx/store-devtools';
import { provideEffects } from '@ngrx/effects';

import { routes } from './app.routes';
import { queueReducer } from './store/queue/queue.reducer';
import { businessReducer } from './store/business/business.reducer';
import { uiReducer } from './store/ui/ui.reducer';
import { QueueEffects } from './store/queue/queue.effects';
import { BusinessEffects } from './store/business/business.effects';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideAnimations(),
    provideHttpClient(),
    provideStore({
      queue: queueReducer,
      business: businessReducer,
      ui: uiReducer
    }),
    provideStoreDevtools({
      maxAge: 25,
      logOnly: !isDevMode()
    }),
    provideEffects([QueueEffects, BusinessEffects])
  ]
};

function isDevMode(): boolean {
  return true; // In production, this would be based on environment
}
