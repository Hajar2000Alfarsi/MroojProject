import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';


@Injectable({
  providedIn: 'root'
})
export class LanguageService {


  constructor(
    private translate: TranslateService
  ) {


    // اللغة الاحتياطية في حال لم توجد ترجمة
    this.translate.setFallbackLang('en');


    // قراءة اللغة المحفوظة سابقاً
    const savedLanguage = localStorage.getItem('language');


    if (savedLanguage) {

      this.changeLanguage(savedLanguage);

    } 
    else {

      this.changeLanguage('en');

    }

  }



  // تغيير لغة الموقع
  changeLanguage(language: string) {


    this.translate.use(language);


    // حفظ اختيار المستخدم
    localStorage.setItem(
      'language',
      language
    );


    // تغيير اتجاه الصفحة
    if (language === 'ar') {

      document.documentElement.dir = 'rtl';

    } 
    else {

      document.documentElement.dir = 'ltr';

    }

  }



  // إرجاع اللغة الحالية
  getCurrentLanguage() {

    return this.translate.currentLang;

  }


}