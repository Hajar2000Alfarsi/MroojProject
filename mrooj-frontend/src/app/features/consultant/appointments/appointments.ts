import { Component, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { AppointmentService } from '../../../core/services/appointment.service';
import { Appointment } from '../../../core/models/api.models';
@Component({selector:'app-consultant-appointments',standalone:true,imports:[CommonModule,DatePipe,TranslatePipe],templateUrl:'./appointments.html',styleUrl:'./appointments.css'})
export class ConsultantAppointments {svc=inject(AppointmentService);items=signal<Appointment[]>([]);load(){this.svc.consultant().subscribe(r=>this.items.set(r.data))}ngOnInit(){this.load()}status(id:number,s:string){this.svc.status(id,s).subscribe(()=>this.load())}}
