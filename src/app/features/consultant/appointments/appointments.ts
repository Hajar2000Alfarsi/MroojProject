import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Appointment,  AppointmentStatus } from '../../../core/models/appointment.model';
import { AppointmentService } from '../../../core/services/appointment.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-appointments',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './appointments.html',
  styleUrl: './appointments.css'
})
export class Appointments implements OnInit {

  private appointmentService = inject(AppointmentService);
  private authService = inject(AuthService);

  appointments = signal<Appointment[]>([]);

  loading = signal(true);

  ngOnInit(): void {

    const consultantId =
      this.authService.currentUser()?.id;

    if (!consultantId) {

      this.loading.set(false);

      return;
    }

    this.appointmentService
      .getConsultantAppointments(consultantId)
      .subscribe({

        next: data => {

          this.appointments.set(data);

          this.loading.set(false);

        },

        error: err => {

          console.error(err);

          this.loading.set(false);

        }

      });

  }
  statusColor(status: AppointmentStatus): string {

  switch (status) {

    case 'CONFIRMED':
      return 'green';

    case 'COMPLETED':
      return 'blue';

    case 'CANCELLED':
      return 'red';

    case 'IN_PROGRESS':
      return 'orange';

    default:
      return 'gray';
  }

}

}