import { Injectable, inject } from '@angular/core'; import { HttpClient, HttpParams } from '@angular/common/http'; import { ApiResponse, Appointment } from '../models/api.models';
import { API_BASE_URL } from '../config/api.config';
@Injectable({providedIn:'root'}) export class AppointmentService {private http=inject(HttpClient);private api=`${API_BASE_URL}/appointments`;
 farmer(){return this.http.get<ApiResponse<Appointment[]>>(`${this.api}/my/farmer`)} consultant(){const now=new Date(),to=new Date();to.setFullYear(to.getFullYear()+1);return this.http.get<ApiResponse<Appointment[]>>(`${this.api}/my/consultant`,{params:new HttpParams().set('from',now.toISOString()).set('to',to.toISOString())})}
 create(v:any){return this.http.post<ApiResponse<Appointment>>(this.api,v)} status(id:number,s:string){return this.http.patch<ApiResponse<Appointment>>(`${this.api}/${id}/status`,{}, {params:{status:s}})}}
