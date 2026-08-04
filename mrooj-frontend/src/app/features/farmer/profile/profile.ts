import {
  Component,
  ElementRef,
  OnDestroy,
  ViewChild,
  effect,
  inject,
  signal
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import * as L from 'leaflet';

import {
  FarmerProfileService,
  FarmerResponseDto
} from '../../../core/services/farmer-profile';


// ===============================
// Leaflet Marker Icon Fix
// ===============================

const defaultIcon = L.icon({

  iconUrl: 'assets/leaflet/marker-icon1.jpg',

  shadowUrl: 'assets/leaflet/marker-shadow.png',

  iconSize: [25, 41],

  iconAnchor: [12, 41],

  popupAnchor: [1, -34],

  shadowSize: [41, 41]

});


L.Marker.prototype.options.icon = defaultIcon;



@Component({

  selector:'app-profile',

  standalone:true,

  imports:[

    CommonModule,

    TranslatePipe

  ],

  templateUrl:'./profile.html',

  styleUrl:'./profile.css'

})


export class Profile implements OnDestroy {


  private farmerProfileService =
    inject(FarmerProfileService);



  profile =
    this.farmerProfileService.profile;



  loading =
    this.farmerProfileService.loading;



  error =
    this.farmerProfileService.error;



  // ===============================
  // Edit Mode
  // ===============================


  editMode =
    signal(false);



  editedProfile =
    signal<FarmerResponseDto | null>(null);



  saving =
    signal(false);



  successMessage =
    signal<string | null>(null);



  // ===============================
  // Validation Errors
  // ===============================


  validationErrors = signal<{

    firstName?: string;

    lastName?: string;

    phone?: string;

    farmName?: string;

    region?: string;

    farmSizeAcres?: string;

    cropTypes?: string;

    location?: string;

  }>({});




  @ViewChild('mapContainer')

  mapContainer?: ElementRef<HTMLDivElement>;



  private map?: L.Map;


  private marker?: L.Marker;



  private mapInitialized = false;



  private mapClickEnabled = false;



  clickedCoordinates =
    signal<{

      lat:number;

      lng:number;

    } | null>(null);





  constructor(){


    this.farmerProfileService.loadProfile();



    effect(()=>{


      const farmer = this.profile();



      if(

        farmer &&

        !this.mapInitialized

      ){


        setTimeout(()=>{


          this.createMap(

            farmer.latitude,

            farmer.longitude,

            farmer.farmName

          );


          this.mapInitialized = true;


        },500);



      }


    });


  }






  // ===============================
  // Start Edit
  // ===============================


  startEdit(){


    const farmer = this.profile();



    if(farmer){


      this.editedProfile.set({

        ...farmer

      });



      this.editMode.set(true);



      this.mapClickEnabled = true;



      this.validationErrors.set({});



      this.successMessage.set(null);


    }


  }






  // ===============================
  // Cancel Edit
  // ===============================


  cancelEdit(){


    this.editedProfile.set(null);



    this.editMode.set(false);



    this.mapClickEnabled = false;



    this.validationErrors.set({});



    const farmer = this.profile();



    if(farmer){


      this.marker?.setLatLng([

        farmer.latitude,

        farmer.longitude

      ]);



      this.map?.setView([

        farmer.latitude,

        farmer.longitude

      ],15);


    }


  }






  // ===============================
  // Update Inputs
  // ===============================

updateField(

  field: keyof FarmerResponseDto,

  value: string | number

){


  if(field === 'email'){

    return;

  }



  const current =

    this.editedProfile();



  if(current){



    this.editedProfile.set({

      ...current,

      [field]: value

    });





    // Remove validation error after editing field

    const errors = {

      ...this.validationErrors()

    };



    if(field in errors){


      delete errors[field as keyof typeof errors];


      this.validationErrors.set(errors);


    }



  }


}
  // ===============================
  // Validation
  // ===============================


  validateProfile(): boolean {


    const profile = this.editedProfile();



    const errors: {

      firstName?: string;

      lastName?: string;

      phone?: string;

      farmName?: string;

      region?: string;

      farmSizeAcres?: string;

      cropTypes?: string;

      location?: string;

    } = {};




    if(!profile){

      return false;

    }



    // First Name

    if(

      !profile.firstName ||

      profile.firstName.trim().length < 2

    ){

      errors.firstName =

        'farmer.profile.errors.firstNameRequired';

    }





    // Last Name

    if(

      !profile.lastName ||

      profile.lastName.trim().length < 2

    ){

      errors.lastName =

        'farmer.profile.errors.lastNameRequired';

    }





    // Phone

    const phoneRegex =

      /^(7|9)[0-9]{7}$/;



    if(

      !profile.phone ||

      !phoneRegex.test(profile.phone)

    ){

      errors.phone =

        'farmer.profile.errors.phoneInvalid';

    }





    // Farm Name

    if(

      !profile.farmName ||

      profile.farmName.trim().length < 2

    ){

      errors.farmName =

        'farmer.profile.errors.farmNameRequired';

    }





    // Region

    if(

      !profile.region ||

      profile.region.trim().length < 2

    ){

      errors.region =

        'farmer.profile.errors.regionRequired';

    }





    // Farm Size

    if(

      !profile.farmSizeAcres ||

      Number(profile.farmSizeAcres) <= 0

    ){

      errors.farmSizeAcres =

        'farmer.profile.errors.farmSizeInvalid';

    }





    // Crop Types

    if(

      !profile.cropTypes ||

      profile.cropTypes.trim().length === 0

    ){

      errors.cropTypes =

        'farmer.profile.errors.cropTypesRequired';

    }





    // Location

    if(

      profile.latitude == null ||

      profile.longitude == null

    ){

      errors.location =

        'farmer.profile.errors.locationRequired';

    }





    this.validationErrors.set(errors);



    return Object.keys(errors).length === 0;


  }








  // ===============================
  // Save Profile
  // ===============================


  async saveProfile(){



    if(!this.validateProfile()){

      return;

    }



    const updated =

      this.editedProfile();



    if(!updated){

      return;

    }




    this.saving.set(true);



    this.successMessage.set(null);





    const request = {



      firstName:

        updated.firstName,



      lastName:

        updated.lastName,



      phone:

        updated.phone,



      farmName:

        updated.farmName,



      region:

        updated.region,



      farmSizeAcres:

        Number(updated.farmSizeAcres),



      cropTypes:

        updated.cropTypes,



      latitude:

        updated.latitude,



      longitude:

        updated.longitude,



      preferredLanguage:

        localStorage.getItem('lang')

        ??

        'ar'


    };





    try {



      await this.farmerProfileService.updateProfile(

        updated.id,

        request

      );





      this.editMode.set(false);



      this.editedProfile.set(null);



      this.mapClickEnabled = false;



      this.validationErrors.set({});





      this.marker?.setLatLng([

        updated.latitude,

        updated.longitude

      ]);





      this.map?.setView([

        updated.latitude,

        updated.longitude

      ],15);





      this.successMessage.set(

        'farmer.profile.success.updated'

      );



    }



    catch(error){



      console.error(

        "Update failed",

        error

      );



      this.error.set(

        'farmer.profile.errors.updateFailed'

      );


    }



    finally{


      this.saving.set(false);


    }


  }








  // ===============================
  // Leaflet Map
  // ===============================


  private createMap(

    latitude:number,

    longitude:number,

    farmName:string

  ){



    const container =

      this.mapContainer?.nativeElement;



    if(!container){

      return;

    }





    this.map = L.map(

      container,

      {

        center:[

          latitude,

          longitude

        ],

        zoom:15,

        zoomControl:true,

        scrollWheelZoom:false

      }

    );





    L.tileLayer(

      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',

      {

        maxZoom:19,

        attribution:

        '&copy; OpenStreetMap contributors'

      }

    )

    .addTo(this.map);






    this.marker = L.marker(

      [

        latitude,

        longitude

      ],

      {

        icon:defaultIcon

      }

    )

    .addTo(this.map)

    .bindPopup(

      `<b>${farmName}</b>`

    );







    this.map.on(

      'click',

      (event:L.LeafletMouseEvent)=>{



        if(!this.mapClickEnabled){

          return;

        }




        const newLat =

          event.latlng.lat;



        const newLng =

          event.latlng.lng;





        this.marker?.setLatLng([

          newLat,

          newLng

        ]);





        const current =

          this.editedProfile();





        if(current){


          this.editedProfile.set({

            ...current,


            latitude:newLat,


            longitude:newLng


          });


        }





        this.clickedCoordinates.set({

          lat:newLat,

          lng:newLng

        });



      }

    );






    setTimeout(()=>{


      this.map?.invalidateSize();


    },500);



  }







  hasValidationErrors(): boolean {


    return Object.keys(

      this.validationErrors()

    ).length > 0;


  }







  ngOnDestroy(){


    this.map?.remove();


  }







  get fullName(){


    const farmer = this.profile();



    if(!farmer){

      return '';

    }



    return `${farmer.firstName} ${farmer.lastName}`;


  }







  onEditProfile(){


    this.startEdit();


  }



}