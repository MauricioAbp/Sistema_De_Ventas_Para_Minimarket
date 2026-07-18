import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse, MfaRequest } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storageKey = 'minimarket_user';

  constructor(private http: HttpClient) {}

  getToken():string|null{
    return this.currentUser()?.token??null;
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/usuario/login`, request).pipe(
      tap((user) => {
        if (user.token) {
          localStorage.setItem(this.storageKey, JSON.stringify(user));
          this.clearPendingMfa();
        } else if (user.mfaRequired) {
          this.setPendingMfa(user);
        }
      })
    );
  }

  verifyMfa(request: MfaRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/login/verify-mfa`, request).pipe(
      tap((user) => {
        if (user.token) {
          localStorage.setItem(this.storageKey, JSON.stringify(user));
          this.clearPendingMfa();
        }
      })
    );
  }

  private readonly pendingMfaKey = 'minimarket_pending_mfa';

  setPendingMfa(user: LoginResponse): void {
    sessionStorage.setItem(this.pendingMfaKey, JSON.stringify(user));
  }

  getPendingMfa(): LoginResponse | null {
    const raw = sessionStorage.getItem(this.pendingMfaKey);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as LoginResponse;
    } catch {
      sessionStorage.removeItem(this.pendingMfaKey);
      return null;
    }
  }

  clearPendingMfa(): void {
    sessionStorage.removeItem(this.pendingMfaKey);
  }

  currentUser(): LoginResponse | null {
    const raw = localStorage.getItem(this.storageKey);
    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as LoginResponse;
    } catch {
      localStorage.removeItem(this.storageKey);
      return null;
    }
  }

  hasRole(roles: string[]): boolean {
    const user = this.currentUser();
    return !!user && roles.includes(user.rol);
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
    this.clearPendingMfa();
  }

  loginErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      const body = error.error;
      if (body && typeof body === 'object' && 'message' in body && typeof body.message === 'string') {
        return body.message;
      }
    }
    return 'No se pudo iniciar sesión';
  }
}
