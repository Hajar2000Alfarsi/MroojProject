import { Component } from '@angular/core';

// Layout component
import { Layout } from './core/layout/layout';


@Component({
  selector: 'app-root',

  standalone: true,

  imports: [
    Layout
  ],

  templateUrl: './app.html',

  styleUrl: './app.css'
})
export class App {

}