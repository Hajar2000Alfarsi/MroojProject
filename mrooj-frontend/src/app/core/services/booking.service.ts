import { Injectable, inject } from '@angular/core'; import { HttpClient, HttpParams } from '@angular/common/http'; import { ApiResponse, Booking, PageResponse } from '../models/api.models';
import { API_BASE_URL } from '../config/api.config';
@Injectable({providedIn:'root'}) export class BookingService {private http=inject(HttpClient);private api=`${API_BASE_URL}/bookings`;
 create(v:any){return this.http.post<ApiResponse<Booking>>(this.api,v)} farmer(){return this.http.get<ApiResponse<PageResponse<Booking>>>(`${this.api}/my/farmer`)} consultant(){return this.http.get<ApiResponse<PageResponse<Booking>>>(`${this.api}/my/consultant`)}
 start(id:number){return this.http.patch<ApiResponse<Booking>>(`${this.api}/${id}/start`,{})} resolve(id:number,text:string){return this.http.patch<ApiResponse<Booking>>(`${this.api}/${id}/consultant-response`,{consultantResponse:text})}
 cancel(id:number,reason=''){return this.http.patch<ApiResponse<Booking>>(`${this.api}/${id}/cancel`,{}, {params:new HttpParams().set('reason',reason)})}}
