import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_BASE_URL } from '../config/api.config';
import { ApiResponse } from '../models/api.models';

export interface ConsultantProfile {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  specialtyDomain: 'PLANT' | 'LIVESTOCK';
  specialtyTags?: string;
  latitude: number;
  longitude: number;
  currentLoad: number;
  experienceYears?: number;
  rating: number;
  totalReviews: number;
  available: boolean;
}

export interface ConsultantProfileUpdate {
  firstName: string;
  lastName: string;
  phone?: string;
  specialtyDomain: 'PLANT' | 'LIVESTOCK';
  specialtyTags?: string;
  experienceYears?: number;
  latitude: number;
  longitude: number;
  available: boolean;
}

@Injectable({ providedIn: 'root' })
export class ConsultantProfileService {
  private readonly http = inject(HttpClient);
  private readonly api = `${API_BASE_URL}/consultants/me`;

  getMyProfile() {
    return this.http.get<ApiResponse<ConsultantProfile>>(this.api);
  }

  updateMyProfile(data: ConsultantProfileUpdate) {
    return this.http.put<ApiResponse<ConsultantProfile>>(this.api, data);
  }
}
