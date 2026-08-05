import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import {
  provideHttpClient,
  withInterceptors
} from '@angular/common/http';

import {
  provideTranslateService
} from '@ngx-translate/core';

import {
  provideTranslateHttpLoader
} from '@ngx-translate/http-loader';

import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor';
export const appConfig: ApplicationConfig = {

  providers: [

    provideBrowserGlobalErrorListeners(),

    provideRouter(routes),

    // CHANGED: was provideHttpClient() with no interceptors — every
    // request went out with no Authorization header. withInterceptors
    // registers authInterceptor so the JWT is attached automatically.
    provideHttpClient(
      withInterceptors([authInterceptor])
    ),

    provideTranslateService({

      loader: provideTranslateHttpLoader({

        prefix: './assets/i18n/',

        suffix: '.json'

      })

    })

  ]

};