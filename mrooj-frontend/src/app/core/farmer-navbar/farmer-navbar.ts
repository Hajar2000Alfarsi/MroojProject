import { Component } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';


interface FarmerInfo {
  fullName: string;
  farmName: string;
  avatarUrl: string | null;
}


@Component({
  selector: 'app-farmer-navbar',
  standalone: true,
  imports: [
    TranslatePipe
  ],
  templateUrl: './farmer-navbar.html',
  styleUrl: './farmer-navbar.css'
})


export class FarmerNavbar {


farmer: FarmerInfo = {

fullName: 'Ahmed Al-Farsi',
farmName: 'Oman Dairy Farm',
avatarUrl: null

};


}