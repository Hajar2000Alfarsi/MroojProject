import { Injectable, inject } from '@angular/core'; import { HttpClient } from '@angular/common/http'; import { ApiResponse, AiAnalysis } from '../models/api.models';
import { API_BASE_URL } from '../config/api.config';
@Injectable({providedIn:'root'}) export class AiAnalysisService { private http=inject(HttpClient); private api=`${API_BASE_URL}/ai`;
 analyze(v:{domain:string;subjectType:string;description:string;image:File}){const f=new FormData();Object.entries(v).forEach(([k,x])=>f.append(k,x as string|Blob));return this.http.post<ApiResponse<AiAnalysis>>(`${this.api}/analyze`,f);}}
