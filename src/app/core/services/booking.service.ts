import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApiResponse } from '../models/user.model';
import { Booking, BookingResolveRequest, Page } from '../models/Booking.model';

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  private apiUrl = 'http://localhost:8080/api/bookings';

  constructor(
    private http: HttpClient
  ) {}

  getById(id: number): Observable<ApiResponse<Booking>> {
    return this.http.get<ApiResponse<Booking>>(`${this.apiUrl}/${id}`);
  }

  /**
   * GET /api/bookings/consultant/{consultantId} — the ONLY backend query
   * that filters bookings by consultant (see BookingRepository). There is
   * no status filter combined with it, so any status-specific view
   * (Requests page = ASSIGNED only, Dashboard's stat counts) fetches this
   * and filters client-side. Default size=100 as a practical ceiling —
   * see the note on Dashboard about what happens past that.
   */
  listByConsultant(consultantId: number, page = 0, size = 100): Observable<ApiResponse<Page<Booking>>> {

    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<ApiResponse<Page<Booking>>>(
      `${this.apiUrl}/consultant/${consultantId}`,
      { params }
    );

  }

  startProgress(bookingId: number, consultantId: number): Observable<ApiResponse<Booking>> {
    return this.http.put<ApiResponse<Booking>>(
      `${this.apiUrl}/${bookingId}/start?consultantId=${consultantId}`,
      {}
    );
  }

  resolve(bookingId: number, consultantId: number, body: BookingResolveRequest): Observable<ApiResponse<Booking>> {
    return this.http.put<ApiResponse<Booking>>(
      `${this.apiUrl}/${bookingId}/resolve?consultantId=${consultantId}`,
      body
    );
  }

}