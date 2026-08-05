import { Component, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { AppointmentService } from '../../../core/services/appointment.service';
import { Appointment } from '../../../core/models/api.models';
@Component({selector:'app-farmer-appointments',standalone:true,imports:[CommonModule,DatePipe,TranslatePipe],templateUrl:'./appointments.html',styleUrl:'./appointments.css'})
export class FarmerAppointments {svc=inject(AppointmentService);items=signal<Appointment[]>([]);ngOnInit(){this.svc.farmer().subscribe(r=>this.items.set(r.data))}}
