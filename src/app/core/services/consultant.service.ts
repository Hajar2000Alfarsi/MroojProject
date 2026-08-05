import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import { ApiResponse } from '../models/user.model';
import { Consultant, ConsultantUpdateRequest } from '../models/consultant.model';/**
 * NOTE ON IDENTITY: the JWT/session only carries the USERS.id — there is
 * no consultant_id anywhere in the auth response. Every consultant page
 * needs the CONSULTANTS.id (for booking/appointment queries), so the
 * very first thing ConsultantLayout does on init is call
 * loadByUserId(currentUser.id) once and cache the result here. Every
 * page under /consultant reads `consultantService.current()` instead of
 * re-fetching.
 */
@Injectable({
  providedIn: 'root'
})
export class ConsultantService {

  private apiUrl = 'http://localhost:8080/api/consultants';

  private readonly _current = signal<Consultant | null>(null);
  readonly current = this._current.asReadonly();

  constructor(
    private http: HttpClient
  ) {}

  loadByUserId(userId: number): Observable<ApiResponse<Consultant>> {

    return this.http.get<ApiResponse<Consultant>>(
      `${this.apiUrl}/by-user/${userId}`
    ).pipe(
      tap(response => this._current.set(response.data))
    );

  }

  getById(id: number): Observable<ApiResponse<Consultant>> {
    return this.http.get<ApiResponse<Consultant>>(`${this.apiUrl}/${id}`);
  }

  updateProfile(id: number, data: ConsultantUpdateRequest): Observable<ApiResponse<Consultant>> {

    return this.http.put<ApiResponse<Consultant>>(
      `${this.apiUrl}/${id}`,
      data
    ).pipe(
      tap(response => this._current.set(response.data))
    );

  }

  setAvailability(id: number, available: boolean): Observable<ApiResponse<Consultant>> {

    return this.http.patch<ApiResponse<Consultant>>(
      `${this.apiUrl}/${id}/availability?available=${available}`,
      {}
    ).pipe(
      tap(response => this._current.set(response.data))
    );

  }

}