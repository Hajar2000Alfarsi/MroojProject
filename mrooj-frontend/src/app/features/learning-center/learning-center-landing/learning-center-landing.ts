import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';


@Component({
  selector: 'app-learning-center-landing',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    TranslatePipe
  ],
  templateUrl: './learning-center-landing.html',
  styleUrl: './learning-center-landing.css'
})


export class LearningCenterLanding {


  categories = [

    {
      title: 'learning.plant.title',
      description: 'learning.plant.description',
      image: 'assets/images/Plant.png',
      link: '/learning-center/articles/plant-care'
    },


    {
      title: 'learning.animal.title',
      description: 'learning.animal.description',
      image: 'assets/images/Animal.png',
      link: '/learning-center/articles/animal-care'
    }

  ];


}