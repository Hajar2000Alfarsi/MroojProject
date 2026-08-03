import { Component, signal } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';


interface DashboardStat {
  titleKey: string;
  value: number;
}


interface BookingRow {
  id: number;
  problemType: string;
  consultant: string;
  status: 'confirmed' | 'pending' | 'completed';
}


interface User {

  id:number;

  email:string;

  firstName:string;

  lastName:string;

  role:string;

}



@Component({

  selector:'app-dashboard',

  standalone:true,

  imports:[
    TranslatePipe
  ],

  templateUrl:'./dashboard.html',

  styleUrl:'./dashboard.css'

})


export class Dashboard {


  user = signal<User | null>(null);



  farmerName = signal('');



  constructor(){


    this.loadUser();


  }




  private loadUser(){


    const storedUser = localStorage.getItem('user');


    if(storedUser){


      const userData:User = JSON.parse(storedUser);


      this.user.set(userData);



      this.farmerName.set(

        `${userData.firstName} ${userData.lastName}`

      );


    }


  }





  stats = signal<DashboardStat[]>([


    {
      titleKey:'farmer.dashboard.stats.totalRequests',
      value:0
    },


    {
      titleKey:'farmer.dashboard.stats.pendingRequests',
      value:0
    },


    {
      titleKey:'farmer.dashboard.stats.resolvedRequests',
      value:0
    },


    {
      titleKey:'farmer.dashboard.stats.upcomingAppointments',
      value:0
    }


  ]);





  bookings = signal<BookingRow[]>([]);





  statusLabelKey(status:BookingRow['status']){


    return `farmer.dashboard.bookings.status.${status}`;


  }


}