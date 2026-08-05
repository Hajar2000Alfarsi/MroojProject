import { Component } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

/** PLACEHOLDER — see requests.ts for the reasoning. Real calendar/status
 *  logic lands when this module is reached in the build plan. */
@Component({
  selector: 'app-consultant-appointments',
  standalone: true,
  imports: [
    TranslatePipe
  ],
  templateUrl: './appointments.html',
  styleUrl: './appointments.css'
})
export class Appointments {}