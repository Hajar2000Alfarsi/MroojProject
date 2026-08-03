import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import {
  FormBuilder,
  FormGroup,
  FormControl,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { TranslatePipe } from '@ngx-translate/core';

import { BookingService } from '../../../core/services/booking.service';
import { Domain, LocationStatusType } from './create-booking.models';

@Component({
  selector: 'app-create-booking',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TranslatePipe
  ],
  templateUrl: './create-booking.html',
  styleUrl: './create-booking.css'
})
export class CreateBooking {

  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly bookingService = inject(BookingService);

  farmerIdControl = new FormControl('', Validators.required);

  selectedDomain = signal<Domain | null>(null);

  latitude: number | null = null;
  longitude: number | null = null;

  locationStatus = signal<LocationStatusType>('idle');

  isLoading = signal(false);
  successMessage = signal('');
  errorMessage = signal('');

  bookingForm: FormGroup;

  constructor() {

    this.bookingForm = this.fb.group({
      subjectType: ['', Validators.required],
      issueCategory: [''],
      cropType: [''],
      description: ['', Validators.required]
    });
  }

  selectDomain(domain: Domain): void {
    this.selectedDomain.set(domain);
  }

  useCurrentLocation(): void {

    if (!navigator.geolocation) {
      this.locationStatus.set('error');
      return;
    }

    this.locationStatus.set('loading');

    navigator.geolocation.getCurrentPosition(
      position => {
        this.latitude = position.coords.latitude;
        this.longitude = position.coords.longitude;
        this.locationStatus.set('success');
      },
      () => {
        this.locationStatus.set('error');
      }
    );
  }

  hasError(field: string, error: string): boolean {
    const control = this.bookingForm.get(field);
    return !!(control?.hasError(error) && control.touched);
  }

  submit(): void {

    this.successMessage.set('');
    this.errorMessage.set('');

    this.farmerIdControl.markAsTouched();

    if (this.bookingForm.invalid || this.farmerIdControl.invalid) {
      this.bookingForm.markAllAsTouched();
      return;
    }

    if (!this.selectedDomain()) {
      this.errorMessage.set('booking.domainRequired');
      return;
    }

    if (this.latitude === null || this.longitude === null) {
      this.errorMessage.set('booking.locationRequired');
      return;
    }

    const value = this.bookingForm.value;

    const payload = {
      title: value.subjectType,
      description: value.description,
      cropType: value.cropType,
      issueCategory: value.issueCategory,
      domain: this.selectedDomain()!,
      subjectType: value.subjectType,
      location: {
        latitude: this.latitude,
        longitude: this.longitude
      }
    };

    this.isLoading.set(true);

    const farmerId = Number(this.farmerIdControl.value);

    this.bookingService.createBooking(farmerId, payload).subscribe({
      next: (response: any) => {
        console.log('Booking created:', response);
        this.successMessage.set('booking.success');
        this.isLoading.set(false);
        this.bookingForm.reset();
        this.farmerIdControl.reset();
        this.selectedDomain.set(null);
      },
      error: (err) => {
        console.error(err);
        this.errorMessage.set('حدث خطأ أثناء إنشاء الحجز');
        this.isLoading.set(false);
      }
    });
  }
}