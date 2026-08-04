import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';


export interface ApiResponse<T> {

  success: boolean;

  message: string;

  data: T;

  timestamp: string;

}



export interface FarmerResponseDto {

  id:number;

  email:string;

  firstName:string;

  lastName:string;

  phone:string;

  farmName:string;

  latitude:number;

  longitude:number;

  region:string;

  farmSizeAcres:number;

  cropTypes:string;

}




// DTO used for UPDATE only
export interface FarmerUpdateRequestDTO {


  firstName?:string;

  lastName?:string;

  phone?:string;

  preferredLanguage?:string;


  farmName?:string;

  latitude?:number;

  longitude?:number;

  region?:string;

  farmSizeAcres?:number;

  cropTypes?:string;

  bio?:string;


}




@Injectable({

  providedIn:'root'

})


export class FarmerProfileService {



  private http = inject(HttpClient);



  profile =
    signal<FarmerResponseDto | null>(null);



  loading =
    signal(false);



  error =
    signal<string | null>(null);



  private apiUrl =
    'http://localhost:8080/api/farmers';







  async loadProfile(){



    const storedUser =
      localStorage.getItem('user');



    if(!storedUser){


      this.error.set(
        'farmer.profile.errors.missingUserId'
      );


      return;

    }





    const user =
      JSON.parse(storedUser);



    const userId =
      user.id;





    this.loading.set(true);

    this.error.set(null);





    try{



      const response =
        await firstValueFrom(


          this.http.get<ApiResponse<FarmerResponseDto>>(

            `${this.apiUrl}/by-user/${userId}`

          )


        );




      this.profile.set(

        response.data

      );




    }


    catch(error){



      console.error(error);



      this.error.set(

        'farmer.profile.errors.loadFailed'

      );



    }



    finally{


      this.loading.set(false);


    }



  }









  async updateProfile(


    farmerId:number,


    data:FarmerUpdateRequestDTO


  ){



    this.loading.set(true);


    this.error.set(null);





    try{



      const response =

        await firstValueFrom(



          this.http.put<ApiResponse<FarmerResponseDto>>(


            `${this.apiUrl}/${farmerId}`,


            data


          )



        );






      this.profile.set(

        response.data

      );





      return response.data;




    }



    catch(error){



      console.error(error);



      this.error.set(

        'farmer.profile.errors.updateFailed'

      );



      throw error;



    }




    finally{


      this.loading.set(false);


    }



  }



}