export type UserRole = 'FARMER' | 'CONSULTANT' | 'ADMIN';

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone: string | null;
  role: UserRole;
  enabled: boolean;
  preferredLanguage: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  user: User;
}

/**
 * Every backend controller wraps its payload in ApiResponse<T>
 * (see com.example.mroojBE.DTOs.ApiResponse). All HTTP services in
 * this app must type their responses as ApiResponse<T>, not T directly,
 * or every read ends up undefined.
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}