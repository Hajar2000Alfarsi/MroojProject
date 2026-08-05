import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from '../services/auth.service';

/**
 * Gate for every route under the 'consultant' layout in app.routes.ts.
 *
 * IMPORTANT LIMITATION: this is a client-side UX gate only. It stops an
 * unauthenticated browser tab from rendering consultant screens; it does
 * NOT stop a farmer or anonymous caller from hitting the consultant API
 * endpoints directly (Postman, curl, etc.), because SecurityConfig on the
 * backend is permitAll() on every path and JwtAuthFilter is never wired
 * into the filter chain yet. Closing that gap requires the backend
 * TODO(PHASE-JWT) work, not anything on this side.
 */
export const consultantGuard: CanActivateFn = () => {

  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn() && auth.role() === 'CONSULTANT') {
    return true;
  }

  return router.createUrlTree(['/auth/login']);

};