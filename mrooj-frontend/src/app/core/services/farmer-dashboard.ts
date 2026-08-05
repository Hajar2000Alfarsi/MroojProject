import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { ApiResponse } from '../models/api.models';

export interface BookingSummary { bookingId:number; problemType:string; consultantName:string; status:string; }
export interface FarmerDashboardResponse {
  farmerId:number; firstName:string; lastName:string; farmName:string;
  totalRequests:number; pendingRequests:number; resolvedRequests:number; upcomingAppointments:number;
  recentBookings: BookingSummary[];
}
@Injectable({providedIn:'root'})
export class FarmerDashboardService {
  private http=inject(HttpClient);
  private apiUrl=`${API_BASE_URL}/farmers/dashboard`;
  getDashboard(userId:number):Observable<FarmerDashboardResponse>{
    return this.http.get<ApiResponse<FarmerDashboardResponse>>(`${this.apiUrl}/${userId}`).pipe(map(r=>r.data));
  }
}
