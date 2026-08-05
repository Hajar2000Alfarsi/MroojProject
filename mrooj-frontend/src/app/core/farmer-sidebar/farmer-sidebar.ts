import { Component, inject, output, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';

import { AuthService } from '../services/auth.service';



@Component({

  selector:'app-farmer-sidebar',

  standalone:true,

  imports:[

    CommonModule,

    RouterLink,

    RouterLinkActive,

    TranslatePipe

  ],

  templateUrl:'./farmer-sidebar.html',

  styleUrl:'./farmer-sidebar.css'

})


export class FarmerSidebar {

  readonly navigate = output<void>();



  private translate = inject(TranslateService);


  private authService = inject(AuthService);


  private router = inject(Router);




  direction = signal<'rtl'|'ltr'>('ltr');




  constructor(){


    this.setDirection();



    this.translate.onLangChange.subscribe(()=>{


      this.setDirection();


    });


  }





  private setDirection(){


    this.direction.set(


      this.translate.currentLang() === 'ar'


      ? 'rtl'


      : 'ltr'


    );


  }







  items = [


    {

      name:'farmer.sidebar.dashboard',

      link:'/farmer/dashboard'

    },


    {

      name:'farmer.sidebar.createBooking',

      link:'/farmer/new-issue'

    },


    {

      name:'farmer.sidebar.myBookings',

      link:'/farmer/bookings'

    },


    {

      name:'farmer.sidebar.appointments',

      link:'/farmer/appointments'

    },


    {

      name:'farmer.sidebar.profile',

      link:'/farmer/profile'

    }


  ];








  closeAfterNavigation(): void {
    this.navigate.emit();
  }

  logout(){


    this.authService.logout();


    this.navigate.emit();
    this.router.navigate(['/auth/login']);


  }



}