import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';


interface FooterLink {

  label: string;

  path: string;

}


@Component({

  selector: 'app-footer',

  standalone: true,

  imports: [
    CommonModule,
    RouterLink,
    TranslatePipe
  ],

  templateUrl: './footer.html',

  styleUrl: './footer.css',

})


export class Footer {


  readonly currentYear = new Date().getFullYear();



  /*
    Navigation links
    روابط الفوتر
  */

  readonly quickLinks: FooterLink[] = [

    {
      label: 'footer.quickLinks.home',
      path: '/'
    },

    {
      label: 'footer.quickLinks.about',
      path: '/about'
    },

    {
      label: 'footer.quickLinks.learning',
      path: '/learning-center'
    }

  ];






  /*
    Services links
    الخدمات
  */

  readonly serviceLinks: FooterLink[] = [

    {
      label: 'footer.services.plant',
      path: '/learning-center/articles/plant-care'
    },

    {
      label: 'footer.services.animal',
      path: '/learning-center/articles/animal-care'
    },

    {
      label: 'footer.services.consultation',
      path: '/consultation'
    }

  ];






  /*
    Contact information
    معلومات التواصل
  */

  readonly contact = {

    location: 'footer.contact.location',

    email: 'footer.contact.email',

    phone: 'footer.contact.phone'

  };


}