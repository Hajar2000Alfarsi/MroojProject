export type Domain = 'PLANT' | 'LIVESTOCK';

export type LocationStatusType = 'idle' | 'loading' | 'success' | 'error';

export interface LocationDto {
  latitude: number | null;
  longitude: number | null;
}

export interface BookingPayload {
  title: string;
  description: string;
  cropType?: string;
  issueCategory?: string;
  domain: Domain;
  subjectType: string;
  symptomsImageUrl?: string;
  location: LocationDto;
}