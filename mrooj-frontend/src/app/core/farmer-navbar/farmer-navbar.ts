import { Component, inject, signal } from '@angular/core';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { LanguageService } from '../services/language';
interface FarmerInfo { fullName:string; farmName:string; avatarUrl:string|null; }
interface StoredUser { firstName:string; lastName:string; }
@Component({selector:'app-farmer-navbar',standalone:true,imports:[TranslatePipe],templateUrl:'./farmer-navbar.html',styleUrl:'./farmer-navbar.css'})
export class FarmerNavbar {
  private translate=inject(TranslateService); private language=inject(LanguageService);
  direction=signal<'rtl'|'ltr'>(this.translate.currentLang() === 'ar'?'rtl':'ltr');
  farmer=signal<FarmerInfo>({fullName:'',farmName:'',avatarUrl:null});
  constructor(){this.translate.onLangChange.subscribe(e=>this.direction.set(e.lang==='ar'?'rtl':'ltr'));this.loadUser();}
  switchLanguage(){this.language.changeLanguage(this.translate.currentLang() === 'ar'?'en':'ar');}
  currentLanguage(){return this.translate.currentLang() === 'ar'?'AR':'EN';}
  private loadUser(){const raw=localStorage.getItem('user');if(raw){const u:StoredUser=JSON.parse(raw);this.farmer.set({fullName:`${u.firstName} ${u.lastName}`,farmName:'',avatarUrl:null});}}
}
