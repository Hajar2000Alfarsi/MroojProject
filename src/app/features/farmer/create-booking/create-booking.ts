import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Domain, BookingPayload } from './create-booking.models';

import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { TranslatePipe } from '@ngx-translate/core';

import { AuthService } from '../../../core/services/auth.service';
import { FarmerService } from '../../../core/services/farmer.service';
import { BookingService } from '../../../core/services/booking.service';
import { FileUploadService } from '../../../core/services/file-upload.service';

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
export class CreateBooking implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly farmerService = inject(FarmerService);
  private readonly bookingService = inject(BookingService);
  private readonly fileUploadService = inject(FileUploadService);

  private readonly backendBaseUrl = 'http://localhost:8080';

  farmerId: number | null = null;

  selectedDomain = signal<Domain | null>(null);

  isLoading = signal(false);
  successMessage = signal('');
  errorMessage = signal('');

  // ===== Image Upload State =====
  isDragOver = signal(false);
  selectedFile = signal<File | null>(null);
  filePreviewUrl = signal<string | null>(null);
  uploadedImageUrl = signal<string | null>(null);
  isUploading = signal(false);
  uploadError = signal('');

  bookingForm: FormGroup;

  categories = ['Disease', 'Pest Infestation', 'Nutrient Deficiency', 'Irrigation Issue', 'Other'];

  constructor() {
    this.bookingForm = this.fb.group({
      subjectType: ['', Validators.required],
      issueCategory: [''],
      cropType: [''],
      description: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    const userId = this.authService.getUserId();
    if (!userId) {
      this.errorMessage.set('لم يتم العثور على المستخدم، الرجاء تسجيل الدخول مرة أخرى');
      return;
    }

    this.farmerService.getByUserId(userId).subscribe({
     next: (farmer) => {
    this.farmerId = farmer.id;
     },
      error: () => {
        this.errorMessage.set('حدث خطأ أثناء تحميل بيانات المزارع');
      }
    });
  }

  selectDomain(domain: Domain): void {
    this.selectedDomain.set(domain);
  }

  // ===== Drag & Drop Handlers =====

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver.set(true);
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver.set(false);
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver.set(false);

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.handleFile(files[0]);
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.handleFile(input.files[0]);
    }
  }

  private handleFile(file: File): void {
    this.uploadError.set('');
    this.uploadedImageUrl.set(null);

    if (!file.type.startsWith('image/')) {
      this.uploadError.set('يرجى اختيار ملف صورة صالح');
      return;
    }

    if (file.size > 10 * 1024 * 1024) {
      this.uploadError.set('حجم الملف يجب ألا يتجاوز 10 ميجابايت');
      return;
    }

    this.selectedFile.set(file);

    const reader = new FileReader();
    reader.onload = () => {
      this.filePreviewUrl.set(reader.result as string);
    };
    reader.readAsDataURL(file);

    this.uploadFile(file);
  }

  private uploadFile(file: File): void {
    this.isUploading.set(true);

    this.fileUploadService.uploadFile(file).subscribe({
      next: (response: any) => {
        const relativeUrl = response.data.url;
        this.uploadedImageUrl.set(`${this.backendBaseUrl}${relativeUrl}`);
        this.isUploading.set(false);
      },
      error: (err) => {
        console.error('Upload failed:', err);
        this.uploadError.set('فشل رفع الصورة، حاول مرة أخرى');
        this.isUploading.set(false);
        this.selectedFile.set(null);
      }
    });
  }

  hasError(field: string, error: string): boolean {
    const control = this.bookingForm.get(field);
    return !!(control?.hasError(error) && control.touched);
  }

  submit(): void {
    this.successMessage.set('');
    this.errorMessage.set('');

    if (this.bookingForm.invalid) {
      this.bookingForm.markAllAsTouched();
      return;
    }

    if (!this.farmerId) {
      this.errorMessage.set('لم يتم تحميل بيانات المزارع بعد');
      return;
    }

    if (!this.selectedDomain()) {
      this.errorMessage.set('الرجاء اختيار نوع الاستشارة');
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
      symptomsImageUrl: this.uploadedImageUrl() || undefined,
    };

    this.isLoading.set(true);

    this.bookingService.createBooking(this.farmerId, payload).subscribe({
      next: (response: any) => {
        console.log('Booking created:', response);
        this.successMessage.set('تم إنشاء الحجز بنجاح');
        this.isLoading.set(false);
        this.bookingForm.reset();
        this.selectedDomain.set(null);
        this.selectedFile.set(null);
        this.filePreviewUrl.set(null);
        this.uploadedImageUrl.set(null);
        this.router.navigate(['/farmer/bookings']);
      },
      error: (err) => {
        console.error(err);
        this.errorMessage.set('حدث خطأ أثناء إنشاء الحجز');
        this.isLoading.set(false);
      }
    });
  }
}