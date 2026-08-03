import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({ providedIn: 'root' })
export class FarmerService {

  private apiUrl = 'http://localhost:8080/api/farmers';

  constructor(private http: HttpClient) {}

  getByUserId(userId: string): Observable<any> {
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/by-user/${userId}`)
      .pipe(map(r => r.data));
  }

  getById(id: string): Observable<any> {
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/${id}`)
      .pipe(map(r => r.data));
  }

  update(id: string, data: any): Observable<any> {
    return this.http.put<ApiResponse<any>>(`${this.apiUrl}/${id}`, data)
      .pipe(map(r => r.data));
  }
}