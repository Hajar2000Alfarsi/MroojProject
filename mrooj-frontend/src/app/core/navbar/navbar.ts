import { Component, EventEmitter, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { LanguageService } from '../services/language';
import { TranslatePipe } from '@ngx-translate/core';
/*
  Define the available languages
  تحديد اللغات المتاحة
*/
type LanguageCode = 'en' | 'ar';

/*
  Interface for navbar links
  شكل بيانات الروابط داخل النافبار
*/
interface NavLink {
  label: string;
  path: string;
}

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    TranslatePipe
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})

export class Navbar {

  constructor(
    private languageService: LanguageService
  ) {}

  /*
    Main navigation links
    روابط التنقل الرئيسية
  */
  readonly navLinks: NavLink[] = [
     {
        label: 'navbar.home',
        path: '/'
      },

      {
        label: 'navbar.about',
        path: '/about'
      },

      {
        label: 'navbar.learning',
        path: '/learning-center'
      }

  ];

  /*
    Controls mobile menu opening/closing
    التحكم بفتح وإغلاق قائمة الهاتف
  */
  readonly isMobileMenuOpen = signal(false);

  /*
    Current selected language
    اللغة الحالية المختارة
  */
  readonly selectedLanguage = signal<LanguageCode>('en');

  /*
    Send selected language to parent component later
    إرسال اللغة المختارة لاحقاً لخدمة الترجمة
  */
  @Output() languageChange = new EventEmitter<LanguageCode>();

  /*
    Open / close mobile menu
    فتح وإغلاق القائمة في الهاتف
  */
  toggleMobileMenu(): void {
    this.isMobileMenuOpen.update(
      (open) => !open
    );
  }

  /*
    Close mobile menu after clicking a link
    إغلاق القائمة بعد اختيار رابط
  */
  closeMobileMenu(): void {
    this.isMobileMenuOpen.set(false);
  }

  /*
    Change language
    تغيير اللغة
  */
  selectLanguage(language: LanguageCode): void {

    // تحديث الزر النشط
    this.selectedLanguage.set(language);

    // تغيير لغة الموقع
    this.languageService.changeLanguage(language);

    // إرسال اللغة إذا احتجناها لاحقاً
    this.languageChange.emit(language);

  }

}