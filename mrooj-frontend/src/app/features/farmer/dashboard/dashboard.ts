import { Component, inject, signal } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { FarmerDashboardService } from '../../../core/services/farmer-dashboard';



interface DashboardStat {

  titleKey: string;

  value: number;

}



interface BookingRow {

  bookingId: number;

  problemType: string;

  consultantName: string;

  status: string;

}



interface User {

  id: number;

  email: string;

  firstName: string;

  lastName: string;

  role: string;

}





@Component({

  selector: 'app-dashboard',

  standalone: true,

  imports: [

    TranslatePipe

  ],

  templateUrl: './dashboard.html',

  styleUrl: './dashboard.css'

})



export class Dashboard {



  private dashboardService = inject(FarmerDashboardService);



  user = signal<User | null>(null);



  farmerName = signal('');




  stats = signal<DashboardStat[]>([


    {

      titleKey: 'farmer.dashboard.stats.totalRequests',

      value: 0

    },


    {

      titleKey: 'farmer.dashboard.stats.pendingRequests',

      value: 0

    },


    {

      titleKey: 'farmer.dashboard.stats.resolvedRequests',

      value: 0

    },


    {

      titleKey: 'farmer.dashboard.stats.upcomingAppointments',

      value: 0

    }


  ]);




  bookings = signal<BookingRow[]>([]);





  constructor(){


    this.loadUser();


  }





  private loadUser(){



    const storedUser = localStorage.getItem('user');



    if(storedUser){



      const userData: User = JSON.parse(storedUser);



      this.user.set(userData);



      this.farmerName.set(

        `${userData.firstName} ${userData.lastName}`

      );



      this.loadDashboard(userData.id);



    }


  }






  private loadDashboard(userId:number){



    this.dashboardService

      .getDashboard(userId)

      .subscribe({



        next:(response)=>{



          console.log(

            "Farmer dashboard response:",

            response

          );




          this.stats.set([



            {

              titleKey:'farmer.dashboard.stats.totalRequests',

              value: response.totalRequests ?? 0

            },



            {

              titleKey:'farmer.dashboard.stats.pendingRequests',

              value: response.pendingRequests ?? 0

            },



            {

              titleKey:'farmer.dashboard.stats.resolvedRequests',

              value: response.resolvedRequests ?? 0

            },



            {

              titleKey:'farmer.dashboard.stats.upcomingAppointments',

              value: response.upcomingAppointments ?? 0

            }



          ]);






          this.bookings.set(

            response.recentBookings ?? []

          );




        },




        error:(error)=>{



          console.error(

            "Failed to load farmer dashboard",

            error

          );


        }



      });



  }







  statusLabelKey(status:string){



    return `farmer.dashboard.bookings.status.${status.toLowerCase()}`;



  }



}