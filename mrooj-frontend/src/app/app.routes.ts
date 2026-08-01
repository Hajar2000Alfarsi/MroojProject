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


  // Learning Center Main Page
  {
    path: 'learning-center',
    loadComponent: () =>
      import('./features/learning-center/learning-center-landing/learning-center-landing')
      .then(m => m.LearningCenterLanding)
  },


  // Learning Articles Page
  {
    path: 'learning-center/articles/:category',
    loadComponent: () =>
      import('./features/learning-center/learning-articles/learning-articles')
      .then(m => m.LearningArticles)
  },

  // Registration Page
{
  path: 'auth/registration',
  loadComponent: () =>
    import('./features/auth/registration/registration')
    .then(m => m.Registration)
},

  // Login Page
{
  path: 'auth/login',
  loadComponent: () =>
    import('./features/auth/login/login')
    .then(m => m.Login)
},

  // أي رابط غير موجود يرجع للـ Home
  {
    path: '**',
    redirectTo: ''
  }

];