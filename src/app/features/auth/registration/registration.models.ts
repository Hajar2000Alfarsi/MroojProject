export type AccountType = 'farmer' | 'consultant';

export type SpecialtyDomain = 'PLANT' | 'LIVESTOCK';

export type LocationStatus =
  | 'idle'
  | 'loading'
  | 'success'
  | 'error';


export interface GeoCoordinates {
  latitude: number | null;
  longitude: number | null;
}


export interface FarmerRegistrationPayload extends GeoCoordinates {

  email: string;
  password: string;

  firstName: string;
  lastName: string;
  phone?: string;

  preferredLanguage: string;

  farmName: string;
  region?: string;

  farmSizeAcres?: number;
  cropTypes?: string;

}


export interface ConsultantRegistrationPayload extends GeoCoordinates {

  email: string;
  password: string;

  firstName: string;
  lastName: string;
  phone?: string;

  preferredLanguage: string;

  specialtyDomain: SpecialtyDomain;

  specialtyTags?: string;

  experienceYears?: number;

}