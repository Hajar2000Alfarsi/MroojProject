import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';



export interface BookingSummary {

  bookingId:number;

  problemType:string;

  consultantName:string;

  status:string;

}



export interface FarmerDashboardResponse {


  farmerId:number;

  firstName:string;

  lastName:string;

  farmName:string;


  totalRequests:number;

  pendingRequests:number;

  resolvedRequests:number;

  upcomingAppointments:number;


  recentBookings: BookingSummary[];

}




@Injectable({
  providedIn:'root'
})
export class FarmerDashboardService {


private http = inject(HttpClient);



private apiUrl =
'http://localhost:8080/api/farmers/dashboard';



getDashboard(userId:number):Observable<FarmerDashboardResponse>{


return this.http.get<FarmerDashboardResponse>(

`${this.apiUrl}/${userId}`

);


}


}