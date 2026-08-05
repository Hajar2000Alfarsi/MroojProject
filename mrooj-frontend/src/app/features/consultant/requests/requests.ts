import { SERVER_BASE_URL } from '../../../core/config/api.config';
import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { BookingService } from '../../../core/services/booking.service';
import { AppointmentService } from '../../../core/services/appointment.service';
import { Booking } from '../../../core/models/api.models';

interface AiReportView {
  summary?: string;
  possibleIssue?: string;
  confidence?: string;
  urgency?: string;
  observations?: string[];
  recommendedActions?: string[];
  missingInformation?: string[];
  disclaimer?: string;
}

@Component({
  selector: 'app-consultant-requests',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslatePipe],
  templateUrl: './requests.html',
  styleUrl: './requests.css'
})
export class ConsultantRequests {
  private readonly svc = inject(BookingService);
  private readonly apps = inject(AppointmentService);
  readonly translate = inject(TranslateService);

  readonly items = signal<Booking[]>([]);
  readonly message = signal('');
  readonly busyBookingId = signal<number | null>(null);
  responses: Record<number, string> = {};
  dates: Record<number, string> = {};
  meetingLinks: Record<number, string> = {};

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.svc.consultant().subscribe({
      next: response => {
        this.items.set(response.data.content);
        for (const booking of response.data.content) {
          if (booking.consultantResponse && !this.responses[booking.id]) {
            this.responses[booking.id] = booking.consultantResponse;
          }
        }
      },
      error: error => this.message.set(this.errorMessage(error, 'Failed to load requests'))
    });
  }

  start(id: number): void {
    this.busyBookingId.set(id);
    this.svc.start(id).subscribe({
      next: () => {
        this.busyBookingId.set(null);
        this.load();
      },
      error: error => {
        this.busyBookingId.set(null);
        this.message.set(this.errorMessage(error, 'Failed to start request'));
      }
    });
  }

  resolve(id: number): void {
    const responseText = this.responses[id]?.trim();
    if (!responseText) return;

    this.busyBookingId.set(id);
    this.svc.resolve(id, responseText).subscribe({
      next: () => {
        this.busyBookingId.set(null);
        this.message.set(this.translate.instant('consultant.requests.saved'));
        this.load();
      },
      error: error => {
        this.busyBookingId.set(null);
        this.message.set(this.errorMessage(error, 'Failed to save response'));
      }
    });
  }

  schedule(booking: Booking): void {
    const scheduledAt = this.dates[booking.id];
    if (!scheduledAt) return;

    this.busyBookingId.set(booking.id);
    this.apps.create({
      bookingId: booking.id,
      scheduledAt,
      durationMinutes: 30,
      meetingLink: this.meetingLinks[booking.id]?.trim() || '',
      location: 'Online',
      notes: 'Consultation appointment'
    }).subscribe({
      next: () => {
        this.busyBookingId.set(null);
        this.message.set(this.translate.instant('consultant.requests.scheduled'));
      },
      error: error => {
        this.busyBookingId.set(null);
        this.message.set(this.errorMessage(
          error,
          this.translate.instant('consultant.requests.scheduleFailed')
        ));
      }
    });
  }

  aiReport(raw?: string): AiReportView | null {
    if (!raw) return null;
    try {
      return JSON.parse(raw) as AiReportView;
    } catch {
      return { summary: raw };
    }
  }

  canEditResponse(booking: Booking): boolean {
    return ['ASSIGNED', 'IN_PROGRESS', 'RESOLVED'].includes(booking.status);
  }

  canSchedule(booking: Booking): boolean {
    return ['ASSIGNED', 'IN_PROGRESS', 'RESOLVED'].includes(booking.status);
  }

  imageUrl(path: string): string {
    if (!path) return '';
    const normalized = path.startsWith('/') ? path : `/${path}`;
    return path.startsWith('http') ? path : `${SERVER_BASE_URL}${normalized}`;
  }

  imageLoadFailed(event: Event): void {
    const image = event.target as HTMLImageElement;
    image.hidden = true;
    const fallback = image.nextElementSibling as HTMLElement | null;
    if (fallback) fallback.hidden = false;
  }

  private errorMessage(error: any, fallback: string): string {
    return error?.error?.message || error?.error?.error || fallback;
  }
}
