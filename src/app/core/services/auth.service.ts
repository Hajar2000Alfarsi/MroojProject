import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import {
  ApiResponse,
  AuthResponse,
  LoginRequest,
  User
} from '../models/user.model';


const TOKEN_KEY = 'mrooj_token';
const USER_KEY = 'mrooj_user';


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';


  private readonly _currentUser = signal<User | null>(
    this.readStoredUser()
  );


  private readonly _token = signal<string | null>(
    localStorage.getItem(TOKEN_KEY)
  );


  readonly currentUser = this._currentUser.asReadonly();


  readonly isLoggedIn = computed(
    () =>
      this._token() !== null &&
      this._currentUser() !== null
  );


  readonly role = computed(
    () => this._currentUser()?.role ?? null
  );


  constructor(
    private http: HttpClient
  ) {}


  login(
    data: LoginRequest
  ): Observable<ApiResponse<AuthResponse>> {

    return this.http.post<ApiResponse<AuthResponse>>(
      `${this.apiUrl}/login`,
      data
    ).pipe(
      tap(response => {
        this.setSession(response.data);
      })
    );

  }


  logout(): void {

    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);

    this._token.set(null);
    this._currentUser.set(null);

  }


  getToken(): string | null {

    return this._token();

  }


  private setSession(
    auth: AuthResponse
  ): void {

    localStorage.setItem(
      TOKEN_KEY,
      auth.token
    );


    localStorage.setItem(
      USER_KEY,
      JSON.stringify(auth.user)
    );


    this._token.set(auth.token);
    this._currentUser.set(auth.user);

  }


  private readStoredUser(): User | null {

    const raw = localStorage.getItem(USER_KEY);


    if (!raw) {
      return null;
    }


    try {

      return JSON.parse(raw) as User;

    } catch {

      return null;

    }

  }

}