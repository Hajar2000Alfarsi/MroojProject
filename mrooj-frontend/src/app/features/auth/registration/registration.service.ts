import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import {
  FarmerRegistrationPayload,
  ConsultantRegistrationPayload
} from './registration.models';

@Injectable({ providedIn: 'root' })
export class RegistrationService {
  private apiUrl = API_BASE_URL;

  constructor(private http: HttpClient) {}

  registerFarmer(data: FarmerRegistrationPayload): Observable<unknown> {
    return this.http.post(`${this.apiUrl}/farmers/register`, data);
  }

  registerConsultant(data: ConsultantRegistrationPayload): Observable<unknown> {
    return this.http.post(`${this.apiUrl}/consultants/register`, data);
  }
}
