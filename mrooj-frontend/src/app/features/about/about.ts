import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';




interface ProcessStep {

  title: string;

  description: string;

}



interface WhyItem {

  icon: string;

  title: string;

  description: string;

}




@Component({

  selector: 'app-about',

  standalone: true,

  imports: [

    CommonModule,
    TranslatePipe

  ],

  templateUrl: './about.html',

  styleUrl: './about.css'

})



export class About {



  constructor(
    private translate: TranslateService
  ) {}




  /*
    Process Section
    خطوات عمل المنصة
  */

  readonly processSteps: ProcessStep[] = [

    {
      title: 'about.process.steps.0.title',
      description: 'about.process.steps.0.description'
    },


    {
      title: 'about.process.steps.1.title',
      description: 'about.process.steps.1.description'
    },


    {
      title: 'about.process.steps.2.title',
      description: 'about.process.steps.2.description'
    }

  ];






  /*
    Why Choose MROOJ Cards

    لماذا تختار مروج
  */

  readonly whyItems: WhyItem[] = [

    {
      icon: '🤖',
      title: 'about.why.items.0.title',
      description: 'about.why.items.0.description'
    },


    {
      icon: '🌱',
      title: 'about.why.items.1.title',
      description: 'about.why.items.1.description'
    },


    {
      icon: '👨‍⚕️',
      title: 'about.why.items.2.title',
      description: 'about.why.items.2.description'
    }

  ];



}