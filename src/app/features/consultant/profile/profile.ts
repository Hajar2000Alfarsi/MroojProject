import { Component } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

/** PLACEHOLDER — see requests.ts for the reasoning. Real profile/
 *  availability form lands when this module is reached in the build plan. */
@Component({
  selector: 'app-consultant-profile',
  standalone: true,
  imports: [
    TranslatePipe
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile {}