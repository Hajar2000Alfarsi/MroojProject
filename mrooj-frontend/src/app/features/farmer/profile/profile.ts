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

import { FarmerProfileService, FarmerResponseDto } from '../../../core/services/farmer-profile';




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








  @ViewChild('mapContainer')

  mapContainer?: ElementRef<HTMLDivElement>;




  private map?: L.Map;


  private marker?: L.Marker;






  clickedCoordinates =
    signal<{

      lat:number;

      lng:number;

    } | null>(null);








  private mapInitialized = false;







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


    }



  }









  // ===============================
  // Cancel Edit
  // ===============================


  cancelEdit(){


    this.editedProfile.set(null);


    this.editMode.set(false);



  }









  // ===============================
  // Update Input Values
  // ===============================


  updateField(

    field:keyof FarmerResponseDto,

    value:string | number

  ){



    const current =
      this.editedProfile();




    if(current){



      this.editedProfile.set({

        ...current,

        [field]:value

      });


    }



  }









  // ===============================
  // Save Profile
  // ===============================

async saveProfile(){


  const updated =
    this.editedProfile();



  if(!updated){

    return;

  }






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





    console.log(

      "Profile updated successfully"

    );



  }



  catch(error){



    console.error(

      "Update failed",

      error

    );



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







    if(this.map){



      this.map.setView(

        [

          latitude,

          longitude

        ],

        15

      );




      this.marker?.setLatLng([

        latitude,

        longitude

      ]);



      this.map.invalidateSize();


      return;


    }








    this.map =
      L.map(

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









    this.marker =

      L.marker(

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









    this.marker.on(

      'click',

      ()=>{


        this.clickedCoordinates.set({

          lat:latitude,

          lng:longitude

        });


      }

    );









    setTimeout(()=>{


      this.map?.invalidateSize();



    },500);



  }









  ngOnDestroy(){



    this.map?.remove();



  }









  get fullName(){



    const farmer =
      this.profile();




    if(!farmer){

      return '';

    }





    return `${farmer.firstName} ${farmer.lastName}`;



  }








  onEditProfile(){


    this.startEdit();


  }



}