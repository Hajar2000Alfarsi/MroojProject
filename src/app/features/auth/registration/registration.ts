import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  TranslateService,
  TranslatePipe
} from '@ngx-translate/core';

import {
  AccountType,
  SpecialtyDomain,
  LocationStatus,
  FarmerRegistrationPayload,
  ConsultantRegistrationPayload
} from './registration.models';

import { RegistrationService } from './registration.service';


@Component({
  selector: 'app-registration',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TranslatePipe
  ],
  templateUrl: './registration.html',
  styleUrl: './registration.css',
})
export class Registration {


  accountType = signal<AccountType>('farmer');

  specialtyDomain = signal<SpecialtyDomain | null>(null);


  isFarmer = computed(() =>
    this.accountType() === 'farmer'
  );


  isConsultant = computed(() =>
    this.accountType() === 'consultant'
  );



  showPassword = signal(false);



  locationStatus = signal<LocationStatus>('idle');


  latitude: number | null = null;

  longitude: number | null = null;



  registrationForm: FormGroup;


  submitted = false;


  loading = signal(false);


  successMessage = signal('');

  errorMessage = signal('');




  constructor(
    private fb: FormBuilder,
    private translate: TranslateService,
    private registrationService: RegistrationService
  ){


    this.registrationForm = this.fb.group({


      firstName:[
        '',
        Validators.required
      ],


      lastName:[
        '',
        Validators.required
      ],


      email:[
        '',
        [
          Validators.required,
          Validators.email
        ]
      ],


      password:[
        '',
        [
          Validators.required,
          Validators.minLength(8),
          Validators.pattern(
            /^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}$/
          )
        ]
      ],



      phone:[
        '',
        [
          Validators.required,
          Validators.pattern(/^[79][0-9]{7}$/)
        ]
      ],



      preferredLanguage:[
        this.translate.currentLang || 'ar'
      ],



      // Farmer

      farmName:[
        ''
      ],


      region:[
        ''
      ],


      farmSizeAcres:[
        null
      ],


      cropTypes:[
        ''
      ],



      // Consultant

      specialtyTags:[
        ''
      ],


      experienceYears:[
        null
      ]


    });



    this.updateValidators('farmer');

  }






  selectAccountType(
    type: AccountType
  ){


    this.accountType.set(type);



    if(type === 'farmer'){

      this.specialtyDomain.set(null);

    }



    this.updateValidators(type);


  }







  selectSpecialtyDomain(
    domain: SpecialtyDomain
  ){


    this.specialtyDomain.set(domain);


  }







  private updateValidators(
    type: AccountType
  ){


    const farmName =
      this.registrationForm.get('farmName');


    const region =
      this.registrationForm.get('region');


    const cropTypes =
      this.registrationForm.get('cropTypes');


    const specialtyTags =
      this.registrationForm.get('specialtyTags');





    if(type === 'farmer'){


      farmName?.setValidators(
        Validators.required
      );


      region?.setValidators(
        Validators.required
      );


      cropTypes?.setValidators(
        Validators.required
      );


      specialtyTags?.clearValidators();


    }

    else{


      farmName?.clearValidators();


      region?.clearValidators();


      cropTypes?.clearValidators();


      specialtyTags?.setValidators(
        Validators.required
      );


    }





    farmName?.updateValueAndValidity();

    region?.updateValueAndValidity();

    cropTypes?.updateValueAndValidity();

    specialtyTags?.updateValueAndValidity();


  }








  togglePassword(){

    this.showPassword.update(
      value => !value
    );

  }









  useCurrentLocation(){


    if(!navigator.geolocation){


      this.locationStatus.set('error');

      return;

    }



    this.locationStatus.set('loading');



    navigator.geolocation.getCurrentPosition(


      position=>{


        this.latitude =
          position.coords.latitude;


        this.longitude =
          position.coords.longitude;



        this.locationStatus.set(
          'success'
        );


      },


      ()=>{


        this.locationStatus.set(
          'error'
        );


      }


    );


  }









  hasError(
    field:string,
    error:string
  ){


    const control =
      this.registrationForm.get(field);



    return control?.hasError(error)
    &&
    (
      control.touched ||
      this.submitted
    );


  }









  submit(){



    console.log(
      'SUBMIT CLICKED'
    );



    this.submitted = true;



    this.successMessage.set('');

    this.errorMessage.set('');





    if(this.registrationForm.invalid){


      console.log(
        'FORM INVALID'
      );



      this.registrationForm.markAllAsTouched();



      return;


    }





    if(
      this.isConsultant()
      &&
      !this.specialtyDomain()
    ){


      this.errorMessage.set(
        'Please select specialty domain'
      );


      return;


    }







    if(
      this.latitude === null ||
      this.longitude === null
    ){


      this.errorMessage.set(
        'Please select your location'
      );


      return;


    }






    const data =
      this.buildPayload();



    console.log(
      'DATA SENT:',
      data
    );




    this.loading.set(true);






    if(this.isFarmer()){



      this.registrationService
      .registerFarmer(
        data as FarmerRegistrationPayload
      )
      .subscribe({



        next:(response)=>{


          console.log(
            response
          );


          this.successMessage.set(
            'Farmer account created successfully'
          );


          this.loading.set(false);


        },



        error:(error)=>{


          console.error(
            error
          );


          this.errorMessage.set(
            'Registration failed'
          );


          this.loading.set(false);


        }



      });



    }

    else {



      this.registrationService
      .registerConsultant(
        data as ConsultantRegistrationPayload
      )
      .subscribe({



        next:(response)=>{


          console.log(
            response
          );


          this.successMessage.set(
            'Consultant account created successfully'
          );


          this.loading.set(false);


        },



        error:(error)=>{


          console.error(
            error
          );


          this.errorMessage.set(
            'Registration failed'
          );


          this.loading.set(false);


        }



      });


    }



  }









  private buildPayload()
  :
  FarmerRegistrationPayload |
  ConsultantRegistrationPayload
  {


    const value =
      this.registrationForm.value;



    const common = {


      email:value.email,


      password:value.password,


      firstName:value.firstName,


      lastName:value.lastName,


      phone:value.phone,


      preferredLanguage:
        value.preferredLanguage,


      latitude:this.latitude,


      longitude:this.longitude


    };





    if(this.isFarmer()){



      return {


        ...common,


        farmName:value.farmName,


        region:value.region,


        farmSizeAcres:value.farmSizeAcres,


        cropTypes:value.cropTypes


      };


    }






    return {


      ...common,


      specialtyDomain:
        this.specialtyDomain()!,


      specialtyTags:
        value.specialtyTags,


      experienceYears:
        value.experienceYears


    };



  }



}