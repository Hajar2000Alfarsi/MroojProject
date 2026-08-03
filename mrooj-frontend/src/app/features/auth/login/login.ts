import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

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


interface User {

  id: number;

  email: string;

  firstName: string;

  lastName: string;

  phone: string;

  role: string;

  enabled: boolean;

  preferredLanguage: string;

}


interface LoginResponse {

  success: boolean;

  message: string;

  data: {

    token: string;

    tokenType: string;

    user: User;

  };

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




  get emailControl(){

    return this.loginForm.get('email');

  }



  get passwordControl(){

    return this.loginForm.get('password');

  }





  togglePasswordVisibility(){

    this.isPasswordVisible.update(
      value => !value
    );

  }





  onSubmit(){


    if(this.loginForm.invalid){

      this.loginForm.markAllAsTouched();

      return;

    }



    this.errorMessage.set(null);

    this.isLoading.set(true);



    const request: LoginRequest = {

      email: this.emailControl?.value,

      password: this.passwordControl?.value

    };




    this.authService.login(request)

    .subscribe({



      next:(response: LoginResponse)=>{


        console.log(
          "Login response:",
          response
        );



        const authData = response.data;



        // Save token

        localStorage.setItem(

          'token',

          authData.token

        );




        // Save user information

        localStorage.setItem(

          'user',

          JSON.stringify(authData.user)

        );




        this.isLoading.set(false);



        const role = authData.user.role;



        switch(role){


          case 'FARMER':

            this.router.navigate([
              '/farmer/dashboard'
            ]);

            break;



          case 'CONSULTANT':

            this.router.navigate([
              '/consultant/dashboard'
            ]);

            break;



          case 'ADMIN':

            this.router.navigate([
              '/admin/dashboard'
            ]);

            break;



          default:

            this.router.navigate([
              '/'
            ]);

        }



      },



      error:(error)=>{


        console.error(
          "Login error:",
          error
        );


        this.errorMessage.set(
          'Invalid email or password'
        );


        this.isLoading.set(false);


      }


    });



  }





  onForgotPassword(){

    console.log(
      "Forgot password clicked"
    );

  }


}