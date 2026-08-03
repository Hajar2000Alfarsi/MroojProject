import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface Farmer {
  id: number;
  farmName: string;
  region: string;
  cropTypes: string;
  farmSizeAcres: number;
}

@Injectable({
  providedIn: 'root'
})
export class FarmerService {

  private readonly apiUrl = 'http://localhost:8080/api/farmers';

  constructor(private http: HttpClient) {}

  getByUserId(userId: number): Observable<Farmer> {
    return this.http
      .get<ApiResponse<Farmer>>(`${this.apiUrl}/by-user/${userId}`)
      .pipe(map(response => response.data));
  }

  getById(id: number): Observable<Farmer> {
    return this.http
      .get<ApiResponse<Farmer>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.data));
  }

  update(id: number, data: Partial<Farmer>): Observable<Farmer> {
    return this.http
      .put<ApiResponse<Farmer>>(`${this.apiUrl}/${id}`, data)
      .pipe(map(response => response.data));
  }

}