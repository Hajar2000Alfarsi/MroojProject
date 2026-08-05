import { Component, computed } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

import { AuthService } from '../services/auth.service';
import { ConsultantService } from '../services/consultant.service';

@Component({
  selector: 'app-consultant-navbar',
  standalone: true,
  imports: [
    TranslatePipe
  ],
  templateUrl: './consultant-navbar.html',
  styleUrl: './consultant-navbar.css'
})
export class ConsultantNavbar {

  constructor(
    private authService: AuthService,
    private consultantService: ConsultantService
  ) {}

  // FarmerNavbar hardcodes a fixed FarmerInfo object with no service
  // behind it — deliberately not copying that here. This reads the
  // actual logged-in user and the cached consultant profile
  // (ConsultantService.current, populated once by ConsultantLayout).

  readonly fullName = computed(() => {
    const user = this.authService.currentUser();
    return user ? `${user.firstName} ${user.lastName}` : '';
  });

  readonly initial = computed(() => this.fullName().charAt(0) || '?');

  readonly specialtyDomain = computed(() =>
    this.consultantService.current()?.specialtyDomain ?? null
  );

  readonly isAvailable = computed(() =>
    this.consultantService.current()?.available ?? false
  );

}