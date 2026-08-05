import { CommonModule, DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { FarmerDashboardService } from '../../../core/services/farmer-dashboard';
import { AppointmentService } from '../../../core/services/appointment.service';
import { Appointment } from '../../../core/models/api.models';

interface DashboardStat { titleKey:string; value:number; }
interface BookingRow { bookingId:number; problemType:string; consultantName:string; status:string; }
interface User { id:number; email:string; firstName:string; lastName:string; role:string; }
@Component({selector:'app-dashboard',standalone:true,imports:[CommonModule,DatePipe,TranslatePipe],templateUrl:'./dashboard.html',styleUrl:'./dashboard.css'})
export class Dashboard {
  private dashboardService=inject(FarmerDashboardService);private appointmentService=inject(AppointmentService);
  user=signal<User|null>(null);farmerName=signal('');appointments=signal<Appointment[]>([]);
  stats=signal<DashboardStat[]>([
    {titleKey:'farmer.dashboard.stats.totalRequests',value:0},{titleKey:'farmer.dashboard.stats.pendingRequests',value:0},{titleKey:'farmer.dashboard.stats.resolvedRequests',value:0},{titleKey:'farmer.dashboard.stats.upcomingAppointments',value:0}
  ]);
  bookings=signal<BookingRow[]>([]);
  constructor(){this.loadUser();this.loadAppointments()}
  private loadUser(){const raw=localStorage.getItem('user');if(!raw)return;const u:User=JSON.parse(raw);this.user.set(u);this.farmerName.set(`${u.firstName} ${u.lastName}`);this.loadDashboard(u.id)}
  private loadDashboard(userId:number){this.dashboardService.getDashboard(userId).subscribe({next:r=>{this.stats.set([{titleKey:'farmer.dashboard.stats.totalRequests',value:r.totalRequests??0},{titleKey:'farmer.dashboard.stats.pendingRequests',value:r.pendingRequests??0},{titleKey:'farmer.dashboard.stats.resolvedRequests',value:r.resolvedRequests??0},{titleKey:'farmer.dashboard.stats.upcomingAppointments',value:r.upcomingAppointments??0}]);this.bookings.set(r.recentBookings??[])},error:e=>console.error('Failed to load farmer dashboard',e)})}
  private loadAppointments(){this.appointmentService.farmer().subscribe({next:r=>this.appointments.set((r.data??[]).filter(a=>new Date(a.scheduledAt).getTime()>=Date.now()).slice(0,5)),error:e=>console.error('Failed to load farmer appointments',e)})}
  statusLabelKey(status:string){return `farmer.dashboard.bookings.status.${status.toLowerCase()}`}
}
