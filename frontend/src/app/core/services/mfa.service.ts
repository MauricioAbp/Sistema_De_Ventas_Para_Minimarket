import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginResponse, MfaRequest } from '../models/auth.models';

@Injectable({
  providedIn: 'root'
})
export class MfaService {
  constructor(private http: HttpClient) {}

  verifyMfa(request: MfaRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/login/verify-mfa`, request);
  }
}
