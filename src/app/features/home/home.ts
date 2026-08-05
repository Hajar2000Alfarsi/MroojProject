import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';


@Component({

  selector: 'app-home',

  standalone: true,

  imports: [
    CommonModule,
    RouterLink,
    TranslatePipe
  ],

  templateUrl: './home.html',

  styleUrl: './home.css'

})

// All text in this component is translated using the ngx-translate library. 
// The translation keys are defined in the translation files located in the assets/i18n folder.
//  The TranslatePipe is used to translate the text in the template.
export class Home {


}