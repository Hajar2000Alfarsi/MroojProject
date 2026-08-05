import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { ConsultantSidebar } from '../../core/consultant-sidebar/consultant-sidebar';
import { ConsultantNavbar } from '../../core/consultant-navbar/consultant-navbar';
import { AuthService } from '../../core/services/auth.service';
import { ConsultantService } from '../../core/services/consultant.service';


@Component({
  selector: 'app-consultant-layout',
  standalone: true,
  imports:[
    RouterOutlet,
    ConsultantSidebar,
    ConsultantNavbar
  ],
  templateUrl:'./consultant-layout.html',
  styleUrl:'./consultant-layout.css'
})
export class ConsultantLayout implements OnInit {


private translate = inject(TranslateService);
private authService = inject(AuthService);
private consultantService = inject(ConsultantService);


direction = signal<'ltr'|'rtl'>('ltr');


constructor(){

this.translate.onLangChange.subscribe(lang=>{

this.direction.set(
lang.lang === 'ar' ? 'rtl':'ltr'
)

})

}


ngOnInit(): void {

  // consultantGuard already confirmed a CONSULTANT session exists before
  // this component can be reached, so currentUser() should be non-null
  // here — checked anyway rather than assumed. This is the ONE place the
  // consultant profile gets fetched; every page under /consultant reads
  // ConsultantService.current() instead of re-fetching by user id.
  const userId = this.authService.currentUser()?.id;

  if (userId != null && this.consultantService.current() === null) {

    this.consultantService.loadByUserId(userId).subscribe({
      error: (err) => console.error('Failed to load consultant profile:', err)
    });

  }

}


}