export type AppointmentStatus =
  | 'SCHEDULED'
  | 'CONFIRMED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'CANCELLED'
  | 'NO_SHOW';

/** Mirrors AppointmentResponseDTO */
export interface Appointment {

  id: number;

  bookingId: number;

  farmerId: number;

  consultantId: number;

  consultantName: string;

  scheduledAt: string;

  endAt: string;

  durationMinutes: number;

  status: AppointmentStatus;

  meetingLink: string | null;

  location: string | null;

  notes: string | null;

  cancellationReason: string | null;

}