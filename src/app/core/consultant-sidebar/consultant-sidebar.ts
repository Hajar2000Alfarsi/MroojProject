import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';

import { AuthService } from '../services/auth.service';

@Component({

selector:'app-consultant-sidebar',

standalone:true,

imports:[
CommonModule,
RouterLink,
RouterLinkActive,
TranslatePipe

],

templateUrl:'./consultant-sidebar.html',

styleUrl:'./consultant-sidebar.css'

})


export class ConsultantSidebar {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

items = [

{
name:'consultant.sidebar.dashboard',
link:'/consultant/dashboard'
},

{
name:'consultant.sidebar.requests',
link:'/consultant/requests'
},

{
name:'consultant.sidebar.appointments',
link:'/consultant/appointments'
},

{
name:'consultant.sidebar.profile',
link:'/consultant/profile'
}

];


logout(){

  // FarmerSidebar.logout() only console.logs — deliberately not copying
  // that here. This actually clears the session; otherwise a stale
  // token in localStorage would still pass consultantGuard on reload.
  this.authService.logout();
  this.router.navigate(['/auth/login']);

}

}