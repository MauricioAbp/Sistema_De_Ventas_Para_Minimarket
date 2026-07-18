import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router) {}

  submit(): void {
    this.error = '';
    this.loading = true;

    this.auth.login({ username: this.username, password: this.password }).subscribe({
      next: (user) => {
        this.loading = false;
        if (user.mfaRequired) {
          void this.router.navigate(['/mfa']);
          return;
        }
        void this.router.navigate(['/app/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.error = this.auth.loginErrorMessage(err);
      }
    });
  }
}
