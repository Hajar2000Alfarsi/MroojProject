import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { TranslatePipe, TranslateService } from '@ngx-translate/core';


interface LoginRequest {
  email: string;
  password: string;
}


interface LoginResponse {
  token: string;
  tokenType: string;
  user: unknown;
}


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
      Here we will connect with backend:

      POST /api/auth/login

      Body:
      {
        email:"",
        password:""
      }

      Response:
      {
        token:"",
        tokenType:"Bearer",
        user:{}
      }

    */



    console.log(
      'Login payload:',
      payload
    );



    // Temporary until AuthService is created

    setTimeout(() => {

      this.isLoading.set(false);

    }, 1000);



  }




  onForgotPassword(): void {

    console.log(
      'Forgot password clicked'
    );

  }


}