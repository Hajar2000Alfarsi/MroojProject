import { Routes } from '@angular/router';

import { Layout } from './core/layout/layout';
import { FarmerLayout } from './layouts/farmer-layout/farmer-layout';

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

      //farmer profile
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/farmer/profile/profile')
            .then(m => m.Profile)
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