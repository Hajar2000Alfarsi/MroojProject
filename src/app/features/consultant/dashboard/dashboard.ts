import { Component, effect, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin } from 'rxjs';

import { ConsultantService } from '../../../core/services/consultant.service';
import { BookingService } from '../../../core/services/booking.service';
import { AppointmentService } from '../../../core/services/appointment.service';
import { Appointment } from '../../../core/models/appointment.model';

interface DashboardStats {
  assigned: number;
  inProgress: number;
  resolved: number;
}

@Component({
  selector: 'app-consultant-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    TranslatePipe
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard {

  private consultantService = inject(ConsultantService);
  private bookingService = inject(BookingService);
  private appointmentService = inject(AppointmentService);

  readonly consultant = this.consultantService.current;

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  readonly stats = signal<DashboardStats>({ assigned: 0, inProgress: 0, resolved: 0 });
  readonly upcomingAppointments = signal<Appointment[]>([]);
  readonly farmerNameByBookingId = signal<Record<number, string>>({});

  private loadedForConsultantId: number | null = null;

  constructor() {

    // ConsultantLayout resolves the consultant profile asynchronously —
    // there's no guarantee it lands before or after this component
    // constructs. This effect fires whenever `consultant()` changes and
    // loads dashboard data exactly once per consultant id, regardless of
    // which order things settle in.
    effect(() => {

      const consultant = this.consultant();

      if (consultant && this.loadedForConsultantId !== consultant.id) {
        this.loadedForConsultantId = consultant.id;
        this.loadDashboard(consultant.id);
      }

    });

  }

  private loadDashboard(consultantId: number): void {

    this.loading.set(true);
    this.error.set(null);

    const now = new Date();
    const sevenDaysOut = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);

    forkJoin({
      bookings: this.bookingService.listByConsultant(consultantId, 0, 100),
      appointments: this.appointmentService.getConsultantCalendar(
        consultantId,
        toLocalIso(now),
        toLocalIso(sevenDaysOut)
      )
    }).subscribe({

      next: ({ bookings, appointments }) => {

        const allBookings = bookings.data.content;

        this.stats.set({
          assigned: allBookings.filter(b => b.status === 'ASSIGNED').length,
          inProgress: allBookings.filter(b => b.status === 'IN_PROGRESS').length,
          resolved: allBookings.filter(b => b.status === 'RESOLVED').length
        });

        // AppointmentResponseDTO has no farmerName field, only farmerId —
        // cross-referencing the booking list already fetched above rather
        // than firing an extra GET per appointment.
        const nameMap: Record<number, string> = {};
        allBookings.forEach(b => { nameMap[b.id] = b.farmerName; });
        this.farmerNameByBookingId.set(nameMap);

        const sorted = [...appointments.data].sort(
          (a, b) => new Date(a.scheduledAt).getTime() - new Date(b.scheduledAt).getTime()
        );
        this.upcomingAppointments.set(sorted.slice(0, 5));

        this.loading.set(false);

      },

      error: (err) => {
        console.error('Failed to load dashboard data:', err);
        this.error.set('consultant.dashboard.loadError');
        this.loading.set(false);
      }

    });

  }

}

/**
 * AppointmentController's /calendar endpoint parses "from"/"to" as
 * java.time.LocalDateTime (no timezone). Date#toISOString() emits a
 * trailing "Z" (UTC) which Spring's ISO.DATE_TIME parser will still
 * accept, but the wall-clock time it represents shifts by the browser's
 * UTC offset — wrong for a consultant in Oman (UTC+4) querying "the next
 * 7 days" in their own local time. This builds the plain local-time ISO
 * string LocalDateTime actually expects (no "Z", no offset).
 */
function toLocalIso(date: Date): string {
  const pad = (n: number) => n.toString().padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
       + `T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
}