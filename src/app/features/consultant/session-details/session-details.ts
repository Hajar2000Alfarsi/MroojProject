import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

/** PLACEHOLDER — see requests.ts for the reasoning. Real session/details
 *  form (treatment plan, meeting link) lands when this module is reached
 *  in the build plan. Reading the route param now so the URL contract
 *  (/consultant/session/:bookingId) doesn't need to change later. */
@Component({
  selector: 'app-consultant-session-details',
  standalone: true,
  imports: [
    TranslatePipe
  ],
  templateUrl: './session-details.html',
  styleUrl: './session-details.css'
})
export class SessionDetails {

  private route = inject(ActivatedRoute);

  readonly bookingId = this.route.snapshot.paramMap.get('bookingId');

}