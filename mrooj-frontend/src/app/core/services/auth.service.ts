import { Injectable, inject } from '@angular/core'; import { HttpClient } from '@angular/common/http'; import { tap } from 'rxjs';
import { ApiResponse, CurrentUser } from '../models/api.models';
import { API_BASE_URL } from '../config/api.config';
@Injectable({providedIn:'root'}) export class AuthService {
 private http=inject(HttpClient); private api=`${API_BASE_URL}/auth`;
 login(data:{email:string;password:string}) { return this.http.post<ApiResponse<{token:string;tokenType:string;user:CurrentUser}>>(`${this.api}/login`,data).pipe(tap(r=>{localStorage.setItem('token',r.data.token);localStorage.setItem('user',JSON.stringify(r.data.user));})); }
 me(){return this.http.get<ApiResponse<CurrentUser>>(`${this.api}/me`);} user():CurrentUser|null{try{return JSON.parse(localStorage.getItem('user')||'null')}catch{return null}}
 logout(){localStorage.removeItem('token');localStorage.removeItem('user');}
}
