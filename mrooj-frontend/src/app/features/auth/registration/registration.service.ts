import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import {
  FarmerRegistrationPayload,
  ConsultantRegistrationPayload
} from './registration.models';

import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class RegistrationService {


  private apiUrl = 'http://localhost:8080/api';


  constructor(
    private http: HttpClient
  ){}



  registerFarmer(
    data: FarmerRegistrationPayload
  ): Observable<any>{

    return this.http.post(
      `${this.apiUrl}/farmers/register`,
      data
    );

  }




  registerConsultant(
    data: ConsultantRegistrationPayload
  ): Observable<any>{

    return this.http.post(
      `${this.apiUrl}/consultants/register`,
      data
    );

  }


}