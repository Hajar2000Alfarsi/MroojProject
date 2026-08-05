import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { RouterLink } from '@angular/router';

@Component({

  selector: 'app-learning-articles',

  standalone: true,

  imports: [
    CommonModule,
    TranslatePipe,
    RouterLink
  ],

  templateUrl: './learning-articles.html',
  styleUrl: './learning-articles.css'

})


export class LearningArticles implements OnInit{


  category = '';

  filteredArticles: any[] = [];

  selectedArticle: any = null;



constructor(
  private route: ActivatedRoute
){}


articles = [

  // 🌱 Plant Articles

  {
    category: 'plant-care',
    title: 'learningArticles.plant.article1.title',
    description: 'learningArticles.plant.article1.description',
    image: 'assets/images/date-palm-diseases.png',
    link: 'https://faculty.uobasrah.edu.iq/uploads/publications/1661535443.pdf'
  },

  {
    category: 'plant-care',
    title: 'learningArticles.plant.article2.title',
    description: 'learningArticles.plant.article2.description',
    image: 'assets/images/red-palm-weevil.jpg',
    link: 'https://openknowledge.fao.org/items/76deda3d-751e-4dde-8f13-fb8b4e4d5ec1'
  },

  {
    category: 'plant-care',
    title: 'learningArticles.plant.article3.title',
    description: 'learningArticles.plant.article3.description',
    image: 'assets/images/crop-disease-prevention.png',
    link: 'https://ipm.ucanr.edu/#gsc.tab=0'
  },

  {
    category: 'plant-care',
    title: 'learningArticles.plant.article4.title',
    description: 'learningArticles.plant.article4.description',
    image: 'assets/images/smart-irrigation.jpg',
    link: 'https://www.youtube.com/watch?v=7j1lMs7fcIQ'
  },

  {
    category: 'plant-care',
    title: 'learningArticles.plant.article5.title',
    description: 'learningArticles.plant.article5.description',
    image: 'assets/images/soil-health.png',
    link: 'https://www.nrcs.usda.gov/conservation-basics/soil/soil-health'
  },

  {
    category: 'plant-care',
    title: 'learningArticles.plant.article6.title',
    description: 'learningArticles.plant.article6.description',
    image: 'assets/images/sustainable-farming.png',
    link: 'https://www.sare.org/'
  },


  // 🐪 Animal Articles

  {
    category: 'animal-care',
    title: 'learningArticles.animal.article1.title',
    description: 'learningArticles.animal.article1.description',
    image: 'assets/images/camel-health.png',
    link: 'https://acsad.org/%D9%85%D9%88%D8%B3%D9%88%D8%B9%D8%A9-%D8%AA%D8%B1%D8%A8%D9%8A%D8%A9-%D8%A7%D9%84%D8%A5%D8%A8%D9%84-%D9%88%D8%A3%D9%85%D8%B1%D8%A7%D8%B6%D9%87%D8%A7-%D8%A7%D9%84%D8%AC%D8%B2%D8%A1-%D8%A7%D9%84%D8%A3/'
  },

  {
    category: 'animal-care',
    title: 'learningArticles.animal.article2.title',
    description: 'learningArticles.animal.article2.description',
    image: 'assets/images/livestock-vaccination.jpg',
    link: 'https://www.merckvetmanual.com/pharmacology/pharmacology-introduction/drug-action-in-animals-pharmacodynamics'
  },

  {
    category: 'animal-care',
    title: 'learningArticles.animal.article3.title',
    description: 'learningArticles.animal.article3.description',
    image: 'assets/images/animal-nutrition.png',
    link: 'https://www.google.com.om/books/edition/Rational_Livestock_Nutrition_in_Rural_Ar/LSjKDwAAQBAJ?hl=ar&gbpv=1'
  },

  {
    category: 'animal-care',
    title: 'learningArticles.animal.article4.title',
    description: 'learningArticles.animal.article4.description',
    image: 'assets/images/disease-detection.jpg',
    link: 'https://pmc.ncbi.nlm.nih.gov/articles/PMC9696233/'
  },

  {
    category: 'animal-care',
    title: 'learningArticles.animal.article5.title',
    description: 'learningArticles.animal.article5.description',
    image: 'assets/images/poultry-health.png',
    link: 'https://www.nadis.org.uk/animal-health-skills/poultry'
  },

  {
    category: 'animal-care',
    title: 'learningArticles.animal.article6.title',
    description: 'learningArticles.animal.article6.description',
    image: 'assets/images/goat-farming.png',
    link: 'https://www.agrifarming.in/goat-farming-business-plan-goat-farm-design'
  }

];

  ngOnInit(){

  this.route.paramMap.subscribe(params => {

    this.category = params.get('category') || '';

    console.log("Category:", this.category);


    this.filteredArticles = this.articles.filter(

      article => article.category === this.category

    );


  });

}


  readArticle(article:any){

    this.selectedArticle = article;

  }

}