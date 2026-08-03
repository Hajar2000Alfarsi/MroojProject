import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookingPayload } from '../../features/farmer/create-booking/create-booking.models';

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  private apiUrl = 'http://localhost:8080/api/bookings';

  constructor(private http: HttpClient) {}

  createBooking(farmerId: number, payload: BookingPayload): Observable<any> {
    return this.http.post(
      `${this.apiUrl}?farmerId=${farmerId}`,
      payload
    );
  }
}