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

import { FarmerProfileService } from '../../../core/services/farmer-profile';



// ===============================
// Fix Leaflet Default Marker Icon
// ===============================

const defaultIcon = L.icon({

  iconUrl: 'assets/leaflet/marker-icon.jpg',


  iconSize: [
    25,
    41
  ],

  iconAnchor: [
    12,
    41
  ],

  popupAnchor: [
    1,
    -34
  ],

  shadowSize: [
    41,
    41
  ]

});


L.Marker.prototype.options.icon = defaultIcon;





@Component({

  selector: 'app-profile',

  standalone: true,

  imports: [

    CommonModule,

    TranslatePipe

  ],

  templateUrl: './profile.html',

  styleUrl: './profile.css'

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




  @ViewChild('mapContainer')

  mapContainer?: ElementRef<HTMLDivElement>;




  private map?: L.Map;


  private marker?: L.Marker;




  clickedCoordinates =
    signal<{

      lat:number,

      lng:number

    } | null>(null);






  constructor(){



    this.farmerProfileService.loadProfile();




    effect(()=>{



      const farmer = this.profile();




      if(farmer){



        setTimeout(()=>{



          this.createMap(

            farmer.latitude,

            farmer.longitude,

            farmer.farmName

          );



        },500);



      }



    });



  }









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






    /*
      If map already created
      update only location
    */


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




      setTimeout(()=>{

        this.map?.invalidateSize();

      },300);



      return;

    }









    /*
      Create Map
    */


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









    /*
      OpenStreetMap Layer
    */


    L.tileLayer(

      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',

      {

        maxZoom:19,

        attribution:
        '&copy; OpenStreetMap contributors'

      }


    )

    .addTo(this.map);









    /*
      Location Marker
    */


    this.marker = L.marker(

      [

        latitude,

        longitude

      ],


      {

        icon: defaultIcon

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









    /*
      Fix Leaflet Rendering
    */


    setTimeout(()=>{


      this.map?.invalidateSize();



    },800);




  }









  ngOnDestroy(): void {



    this.map?.remove();



  }









  get fullName():string{



    const farmer =
      this.profile();




    if(!farmer){


      return '';

    }




    return `${farmer.firstName} ${farmer.lastName}`;



  }









  onEditProfile(){



    console.log(

      "Edit profile clicked"

    );



  }




}