import { Component, inject, signal } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { BookingService } from '../../../core/services/booking.service';
import { AppointmentService } from '../../../core/services/appointment.service';
@Component({selector:'app-consultant-dashboard',standalone:true,imports:[TranslatePipe],templateUrl:'./dashboard.html',styleUrl:'./dashboard.css'})
export class ConsultantDashboard {b=signal(0);a=signal(0);bs=inject(BookingService);as=inject(AppointmentService);ngOnInit(){this.bs.consultant().subscribe(r=>this.b.set(r.data.totalElements));this.as.consultant().subscribe(r=>this.a.set(r.data.length))}}
