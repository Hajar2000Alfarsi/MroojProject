import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { FarmerSidebar } from '../../core/farmer-sidebar/farmer-sidebar';
import { FarmerNavbar } from '../../core/farmer-navbar/farmer-navbar';

@Component({
  selector: 'app-farmer-layout',
  standalone: true,
  imports: [RouterOutlet, FarmerSidebar, FarmerNavbar],
  templateUrl: './farmer-layout.html',
  styleUrl: './farmer-layout.css'
})
export class FarmerLayout {
  private readonly translate = inject(TranslateService);

  readonly direction = signal<'ltr' | 'rtl'>(
    this.translate.currentLang() === 'ar' ? 'rtl' : 'ltr'
  );
  readonly menuOpen = signal(false);

  constructor() {
    this.translate.onLangChange.subscribe(event => {
      this.direction.set(event.lang === 'ar' ? 'rtl' : 'ltr');
    });
  }

  toggleMenu(): void {
    this.menuOpen.update(value => !value);
  }

  closeMenu(): void {
    this.menuOpen.set(false);
  }
}
