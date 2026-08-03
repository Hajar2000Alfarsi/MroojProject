import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';

@Component({

selector:'app-farmer-sidebar',

standalone:true,

imports:[
CommonModule,
RouterLink,
RouterLinkActive,
TranslatePipe

],

templateUrl:'./farmer-sidebar.html',

styleUrl:'./farmer-sidebar.css'

})


export class FarmerSidebar {


items = [

{
name:'farmer.sidebar.dashboard',
link:'/farmer/dashboard'
},

{
name:'farmer.sidebar.createBooking',
link:'/farmer/create-booking'
},

{
name:'farmer.sidebar.myBookings',
link:'/farmer/bookings'
},

{
name:'farmer.sidebar.profile',
link:'/farmer/profile'
}

];


logout(){

console.log("logout");

}

}
