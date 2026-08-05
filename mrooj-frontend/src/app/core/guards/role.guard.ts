import { inject } from '@angular/core'; import { CanActivateFn, Router } from '@angular/router';
export const roleGuard = (role:string): CanActivateFn => () => {
  const raw=localStorage.getItem('user'); const router=inject(Router);
  try { return raw && JSON.parse(raw).role===role ? true : router.createUrlTree(['/auth/login']); } catch { return router.createUrlTree(['/auth/login']); }
};
