export type SpecialtyDomain = 'PLANT' | 'LIVESTOCK';

/** Mirrors ConsultantResponseDTO. */
export interface Consultant {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone: string | null;
  specialtyDomain: SpecialtyDomain;
  specialtyTags: string | null;
  latitude: number | null;
  longitude: number | null;
  currentLoad: number;
  experienceYears: number | null;
  rating: number;
  totalReviews: number;
  available: boolean;
}

/**
 * Body for PUT /api/consultants/{id}.
 *
 * BACKEND GAP: PUT /consultants/{id} reuses ConsultantRequestDTO, which
 * has @NotBlank on both email and password — there is no separate
 * "update" DTO. ConsultantService.updateConsultantProfile() never reads
 * either field back out (it only touches specialtyDomain/specialtyTags/
 * experienceYears/location/name/phone), so the values are validated then
 * discarded. In practice this means every profile-update PUT must still
 * send non-blank email/password or the request 400s at validation,
 * even though neither is used. The Profile page (module 4) will send the
 * consultant's known email plus a placeholder password value to satisfy
 * validation — this is a workaround for a real backend defect, not a
 * frontend design choice. The correct fix is a dedicated
 * ConsultantUpdateRequestDTO without those two @NotBlank fields.
 */
export interface ConsultantUpdateRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
  preferredLanguage?: string;
  specialtyDomain: SpecialtyDomain;
  specialtyTags?: string;
  latitude: number;
  longitude: number;
  experienceYears?: number;
  bio?: string;
  qualifications?: string;
}