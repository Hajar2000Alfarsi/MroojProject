import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApiResponse } from '../models/user.model';
import { Appointment, AppointmentRequest, AppointmentStatus } from '../models/appointment.model';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {

  private apiUrl = 'http://localhost:8080/api/appointments';

  constructor(
    private http: HttpClient
  ) {}

  getById(id: number): Observable<ApiResponse<Appointment>> {
    return this.http.get<ApiResponse<Appointment>>(`${this.apiUrl}/${id}`);
  }

  /**
   * Calendar view — backs both the Dashboard's "next 7 days" widget and
   * the full Appointments/Calendar page (module 5), just with a different
   * date range. from/to must be plain LocalDateTime-format ISO strings —
   * see the toLocalIso() note in dashboard.ts for why Date#toISOString()
   * is wrong here.
   */
  getConsultantCalendar(consultantId: number, from: string, to: string): Observable<ApiResponse<Appointment[]>> {

    const params = new HttpParams()
      .set('from', from)
      .set('to', to);

    return this.http.get<ApiResponse<Appointment[]>>(
      `${this.apiUrl}/consultant/${consultantId}/calendar`,
      { params }
    );

  }

  schedule(request: AppointmentRequest): Observable<ApiResponse<Appointment>> {
    return this.http.post<ApiResponse<Appointment>>(this.apiUrl, request);
  }

  updateStatus(id: number, status: AppointmentStatus): Observable<ApiResponse<Appointment>> {
    return this.http.patch<ApiResponse<Appointment>>(
      `${this.apiUrl}/${id}/status?status=${status}`,
      {}
    );
  }

  cancel(id: number): Observable<ApiResponse<Appointment>> {
    return this.http.put<ApiResponse<Appointment>>(`${this.apiUrl}/${id}/cancel`, {});
  }

}