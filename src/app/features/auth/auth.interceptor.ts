import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { AuthService } from '../../core/services/auth.service';
/**
 * Attaches "Authorization: Bearer <token>" to every request once a
 * session exists. Registered via provideHttpClient(withInterceptors([...]))
 * in app.config.ts — see that file for the wiring.
 *
 * NOTE: this only matters once JwtAuthFilter is actually registered in
 * SecurityConfig's filter chain on the backend (currently permitAll()
 * everywhere, per that file's own TODO(PHASE-JWT) comments). Sending the
 * header now is still correct — it's forward-compatible and harmless
 * against the current permitAll() config — but it is not yet enforced
 * server-side.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const authService = inject(AuthService);
  const token = authService.getToken();

  const publicEndpoints = [
    '/auth/login',
    '/auth/register'
  ];

  if (
    !token ||
    publicEndpoints.some(url => req.url.includes(url))
  ) {
    return next(req);
  }


  const authorizedRequest = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authorizedRequest);

};