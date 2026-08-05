import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/user.model';

import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { TranslatePipe, TranslateService } from '@ngx-translate/core';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    TranslatePipe
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly translate = inject(TranslateService);
  private readonly authService = inject(AuthService);

  get currentDirection(): 'rtl' | 'ltr' {

  return this.translate.currentLang() === 'ar'
    ? 'rtl'
    : 'ltr';

}

  readonly isPasswordVisible = signal(false);

  readonly isLoading = signal(false);

  readonly errorMessage = signal<string | null>(null);



  readonly loginForm: FormGroup = this.fb.group({

    email: [
      '',
      [
        Validators.required,
        Validators.email
      ]
    ],

    password: [
      '',
      [
        Validators.required
      ]
    ]

  });



  get emailControl() {
    return this.loginForm.get('email');
  }


  get passwordControl() {
    return this.loginForm.get('password');
  }



  togglePasswordVisibility(): void {

    this.isPasswordVisible.update(
      visible => !visible
    );

  }



  onSubmit(): void {


    if (this.loginForm.invalid) {

      this.loginForm.markAllAsTouched();

      return;

    }


    this.errorMessage.set(null);

    this.isLoading.set(true);



    const payload: LoginRequest = {

      email: this.emailControl?.value,

      password: this.passwordControl?.value

    };


    /*
      POST /api/auth/login

      Body: { email, password }

      Response (ApiResponse<AuthResponseDTO> envelope — every controller
      wraps its payload this way, see ApiResponse.java):
      {
        success: true,
        message: "Login successful",
        data: { token, tokenType, user: {...} },
        timestamp: "..."
      }

      AuthService.login() already persists token + user to storage via
      an internal tap() — this handler only needs to react to success/
      failure and route by role.
    */

    this.authService.login(payload).subscribe({

      next: (response) => {

        this.isLoading.set(false);

        const role = response.data.user.role;

        if (role === 'CONSULTANT') {
          this.router.navigate(['/consultant/dashboard']);
        } else if (role === 'FARMER') {
          this.router.navigate(['/farmer/dashboard']);
        } else {
          this.router.navigate(['/']);
        }

      },


      error: (error) => {

        console.error(
          'Login failed:',
          error
        );

        this.errorMessage.set(
          'login.errors.invalidLogin'
        );

        this.isLoading.set(false);

      }

    });


  }




  onForgotPassword(): void {

    console.log(
      'Forgot password clicked'
    );

  }


}