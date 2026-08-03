import { Component, inject, signal } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';


interface FarmerInfo {

  fullName: string;

  farmName: string;

  avatarUrl: string | null;

}


interface StoredUser {

  id: number;

  email: string;

  firstName: string;

  lastName: string;

  phone: string;

  role: string;

  enabled: boolean;

  preferredLanguage: string;

}



@Component({

  selector:'app-farmer-navbar',

  standalone:true,

  imports:[],

  templateUrl:'./farmer-navbar.html',

  styleUrl:'./farmer-navbar.css'

})


export class FarmerNavbar {


private translate = inject(TranslateService);



direction = signal<'rtl'|'ltr'>('ltr');



farmer = signal<FarmerInfo>({

  fullName:'',

  farmName:'',

  avatarUrl:null

});





constructor(){


/*
  تغيير اتجاه الناف بار حسب اللغة
*/

this.direction.set(

  this.translate.currentLang() === 'ar'
  ? 'rtl'
  : 'ltr'

);



this.translate.onLangChange.subscribe(lang=>{


this.direction.set(

  lang.lang === 'ar'
  ? 'rtl'
  : 'ltr'

);


});





/*
  قراءة بيانات المستخدم من localStorage
*/

this.loadUser();



}





private loadUser(){


const userData = localStorage.getItem('user');



if(userData){


const user:StoredUser = JSON.parse(userData);



this.farmer.set({

fullName:
`${user.firstName} ${user.lastName}`,

farmName:
'',

avatarUrl:null

});


}



}



}