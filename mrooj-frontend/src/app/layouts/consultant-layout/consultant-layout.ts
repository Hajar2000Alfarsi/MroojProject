import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { AuthService } from '../../core/services/auth.service';
import { LanguageService } from '../../core/services/language';

@Component({
  selector:'app-consultant-layout',
  standalone:true,
  imports:[RouterOutlet,RouterLink,RouterLinkActive,TranslatePipe],
  templateUrl:'./consultant-layout.html',
  styleUrl:'./consultant-layout.css'
})
export class ConsultantLayout {
  readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly language = inject(LanguageService);
  private readonly translate = inject(TranslateService);

  readonly direction = signal<'ltr'|'rtl'>(this.translate.currentLang() === 'ar' ? 'rtl' : 'ltr');
  readonly menuOpen = signal(false);

  constructor() {
    this.translate.onLangChange.subscribe(event => this.direction.set(event.lang === 'ar' ? 'rtl' : 'ltr'));
  }

  toggleMenu(): void { this.menuOpen.update(value => !value); }
  closeMenu(): void { this.menuOpen.set(false); }
  switchLanguage(): void { this.language.changeLanguage(this.translate.currentLang() === 'ar' ? 'en' : 'ar'); }
  currentLanguage(): 'AR'|'EN' { return this.translate.currentLang() === 'ar' ? 'AR' : 'EN'; }
  logout(): void { this.auth.logout(); this.router.navigate(['/auth/login']); }
}
