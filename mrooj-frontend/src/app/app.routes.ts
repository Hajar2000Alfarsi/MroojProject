import { Routes } from '@angular/router';
import { Home } from './features/home/home';

export const routes: Routes = [

  {
    path: '',
    component: Home
  },


  {
    path: 'about',
    loadComponent: () =>
      import('./features/about/about')
      .then(m => m.About)
  },


  {
    path: 'learning-center',
    loadComponent: () =>
      import('./features/learning-center/learning-center-landing/learning-center-landing')
      .then(m => m.LearningCenterLanding)
  }

];