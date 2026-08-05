import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { BookingService } from '../../../core/services/booking.service';
import { Booking } from '../../../core/models/api.models';
import { SERVER_BASE_URL } from '../../../core/config/api.config';

@Component({
  selector: 'app-farmer-bookings',
  standalone: true,
  imports: [CommonModule, TranslatePipe],
  templateUrl: './bookings.html',
  styleUrl: './bookings.css'
})
export class FarmerBookings {
  private svc = inject(BookingService);
  private translate = inject(TranslateService);

  items = signal<Booking[]>([]);
  loading = signal(true);

  ngOnInit(): void {
    this.svc.farmer().subscribe({
      next: response => {
        this.items.set(response.data.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  imageUrl(path?: string): string {
    if (!path) return '';
    if (/^https?:\/\//i.test(path)) return path;
    const normalized = path.startsWith('/') ? path : `/${path}`;
    return `${SERVER_BASE_URL}${normalized}`;
  }

  cancel(id: number): void {
    this.svc.cancel(id, this.translate.instant('farmer.bookings.cancelReason'))
      .subscribe(() => this.ngOnInit());
  }
}
