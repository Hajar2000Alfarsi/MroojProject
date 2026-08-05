import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { Appointment } from '../models/appointment.model';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {

  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/api/appointments';

  getConsultantAppointments(
    consultantId: number
  ): Observable<Appointment[]> {

    return this.http.get<Appointment[]>(
      `${this.apiUrl}/consultant/${consultantId}`
    );

  }

  getAppointmentById(
    id: number
  ): Observable<Appointment> {

    return this.http.get<Appointment>(
      `${this.apiUrl}/${id}`
    );

  }

}