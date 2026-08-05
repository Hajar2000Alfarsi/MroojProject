export type Domain = 'PLANT' | 'LIVESTOCK';

export type BookingStatus =
  | 'PENDING'
  | 'ASSIGNED'
  | 'IN_PROGRESS'
  | 'RESOLVED'
  | 'CANCELLED'
  | 'REJECTED';

export interface LocationDto {
  latitude: number | null;
  longitude: number | null;
}

/** Mirrors BookingResponseDTO exactly, field for field. */
export interface Booking {
  id: number;
  farmerId: number;
  farmerName: string;
  consultantId: number | null;
  consultantName: string | null;
  domain: Domain;
  subjectType: string;
  issueCategory: string | null;
  description: string;
  symptomsImageUrl: string | null;
  aiReport: string | null;
  cropType: string | null;
  location: LocationDto;
  consultantResponse: string | null;
  status: BookingStatus;
  createdAt: string; // ISO-8601 datetime string
  rejectionReason: string | null;
}

/** Body for PUT /api/bookings/{id}/resolve */
export interface BookingResolveRequest {
  consultantResponse: string;
}

/**
 * Matches Spring Data's Page<T> JSON serialization shape.
 * Only the fields this app actually reads are declared — Spring's real
 * payload has more (pageable, sort, etc.) but TS structural typing
 * doesn't require declaring fields you never touch.
 */
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // current page index, 0-based
  size: number;
  first: boolean;
  last: boolean;
}
