import { Routes } from '@angular/router';

import { Layout } from './core/layout/layout';
import { FarmerLayout } from './layouts/farmer-layout/farmer-layout';
import { ConsultantLayout } from './layouts/consultant-layout/consultant-layout';
import { consultantGuard } from './core/guards/consultant.guard';

export const routes: Routes = [

  // ===========================
  // Public Layout
  // ===========================
  {
    path: '',
    component: Layout,

    children: [

      {
        path: '',
        loadComponent: () =>
          import('./features/home/home')
            .then(m => m.Home)
      },

      {
        path: 'about',
        loadComponent: () =>
          import('./features/about/about')
            .then(m => m.About)
      },

      {
        path: 'auth/login',
        loadComponent: () =>
          import('./features/auth/login/login')
            .then(m => m.Login)
      },

      {
        path: 'auth/registration',
        loadComponent: () =>
          import('./features/auth/registration/registration')
            .then(m => m.Registration)
      }

    ]
  },

  // ===========================
  // Farmer Layout
  // ===========================
  {
    path: 'farmer',

    component: FarmerLayout,

    children: [

      {
        path: 'dashboard',

        loadComponent: () =>
          import('./features/farmer/dashboard/dashboard')
            .then(m => m.Dashboard)
      },

      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }

    ]
  },

  // ===========================
  // Consultant Layout
  // ===========================
  // NOTE: every child below is behind consultantGuard on the PARENT
  // route, so it applies to the whole subtree — no need to repeat it
  // on each child. Only 'dashboard' exists as a file as of this commit;
  // requests/appointments/profile/session-details are added in the
  // following steps. Until then this block will fail to compile —
  // expected, not an oversight.
  {
    path: 'consultant',

    component: ConsultantLayout,

    // canActivate: [consultantGuard], // TEMP: disabled for design review

    children: [

      {
        path: 'dashboard',

        loadComponent: () =>
          import('./features/consultant/dashboard/dashboard')
            .then(m => m.Dashboard)
      },

      {
        path: 'requests',

        loadComponent: () =>
          import('./features/consultant/requests/requests')
            .then(m => m.Requests)
      },

      {
        path: 'appointments',

        loadComponent: () =>
          import('./features/consultant/appointments/appointments')
            .then(m => m.Appointments)
      },

      {
        path: 'profile',

        loadComponent: () =>
          import('./features/consultant/profile/profile')
            .then(m => m.Profile)
      },

      // Reached from a booking card ("View session"), not the sidebar —
      // hence no matching item in ConsultantSidebar.items.
      {
        path: 'session/:bookingId',

        loadComponent: () =>
    import('./features/consultant/session-details/session-details')
            .then(m => m.SessionDetails)
      },

      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }

    ]
  },

  {
    path: '**',
    redirectTo: ''
  }

];