import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-mfa',
  standalone: true,
  imports: [FormsModule, NgIf],
  templateUrl: './mfa.component.html',
  styleUrl: './mfa.component.css'
})
export class MfaComponent {
  code = '';
  error = '';
  loading = false;
  usuarioId: number | null = null;
  username = '';

  constructor(private auth: AuthService, private router: Router) {
    const pending = this.auth.getPendingMfa();
    if (pending && pending.mfaRequired) {
      this.usuarioId = pending.idUsuario;
      this.username = pending.username;
    }

    if (!this.usuarioId) {
      void this.router.navigate(['/login']);
    }
  }

  submit(): void {
    if (!this.usuarioId) {
      return;
    }

    this.error = '';
    this.loading = true;

    this.auth.verifyMfa({ usuarioId: this.usuarioId, code: this.code }).subscribe({
      next: () => {
        this.loading = false;
        void this.router.navigate(['/app/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.error = this.auth.loginErrorMessage(err);
      }
    });
  }
}
