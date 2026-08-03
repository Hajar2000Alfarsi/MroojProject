export type Domain = 'PLANT' | 'LIVESTOCK';



export interface BookingPayload {
  title: string;
  description: string;
  cropType?: string;
  issueCategory?: string;
  domain: Domain;
  subjectType: string;
  symptomsImageUrl?: string;
}