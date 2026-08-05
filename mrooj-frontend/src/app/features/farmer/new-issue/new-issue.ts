import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import * as L from 'leaflet';
import { AiAnalysisService } from '../../../core/services/ai-analysis.service';
import { BookingService } from '../../../core/services/booking.service';
import { AiAnalysis } from '../../../core/models/api.models';

@Component({ selector:'app-new-issue', standalone:true, imports:[CommonModule,ReactiveFormsModule,TranslatePipe], templateUrl:'./new-issue.html', styleUrl:'./new-issue.css' })
export class NewIssue implements AfterViewInit, OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly ai = inject(AiAnalysisService);
  private readonly bookings = inject(BookingService);
  readonly translate = inject(TranslateService);

  @ViewChild('issueMap') issueMap?: ElementRef<HTMLDivElement>;
  private map?: L.Map;
  private marker?: L.Marker;

  readonly result = signal<AiAnalysis | null>(null);
  readonly loading = signal(false);
  readonly submitting = signal(false);
  readonly locating = signal(false);
  readonly locationMessage = signal('');
  readonly message = signal('');
  readonly success = signal(false);
  file: File | null = null;

  readonly form = this.fb.group({
    domain: ['PLANT', Validators.required],
    subjectType: ['', Validators.required],
    issueCategory: [''],
    description: ['', [Validators.required, Validators.minLength(10)]],
    latitude: [23.588, Validators.required],
    longitude: [58.382, Validators.required]
  });

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.createMap();
      this.locateCurrentPosition(false);
    }, 0);
  }

  ngOnDestroy(): void { this.map?.remove(); }

  useCurrentLocation(): void { this.locateCurrentPosition(true); }

  private locateCurrentPosition(showFeedback: boolean): void {
    if (!navigator.geolocation) {
      if (showFeedback) this.locationMessage.set(this.translate.instant('common.locationUnavailable'));
      return;
    }
    this.locating.set(true);
    this.locationMessage.set('');
    navigator.geolocation.getCurrentPosition(
      position => {
        this.updateLocation(L.latLng(position.coords.latitude, position.coords.longitude), true);
        this.locating.set(false);
        if (showFeedback) this.locationMessage.set(this.translate.instant('common.locationDetected'));
      },
      () => {
        this.locating.set(false);
        if (showFeedback) this.locationMessage.set(this.translate.instant('common.locationPermissionDenied'));
      },
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 60000 }
    );
  }

  private createMap(): void {
    if (!this.issueMap || this.map) return;
    const lat = Number(this.form.value.latitude) || 23.588;
    const lng = Number(this.form.value.longitude) || 58.382;
    this.map = L.map(this.issueMap.nativeElement, { center:[lat,lng], zoom:13, scrollWheelZoom:true });
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom:19, attribution:'&copy; OpenStreetMap contributors' }).addTo(this.map);
    this.marker = L.marker([lat,lng], { icon:this.mapIcon(), draggable:true, title:this.translate.instant('farmer.newIssue.location') }).addTo(this.map);
    this.map.on('click', event => this.updateLocation(event.latlng, false));
    this.marker.on('dragend', () => this.updateLocation(this.marker!.getLatLng(), false));
    setTimeout(() => this.map?.invalidateSize(), 150);
  }

  private mapIcon(): L.DivIcon {
    return L.divIcon({ className:'', html:'<div style="width:26px;height:26px;background:#1e4620;border:4px solid #fff;border-radius:50% 50% 50% 0;transform:rotate(-45deg);box-shadow:0 3px 10px rgba(0,0,0,.38)"></div>', iconSize:[26,26], iconAnchor:[13,26] });
  }

  private updateLocation(point: L.LatLng, centerMap: boolean): void {
    this.form.patchValue({ latitude:point.lat, longitude:point.lng });
    this.marker?.setLatLng(point);
    if (centerMap && this.map) this.map.setView(point, Math.max(this.map.getZoom(), 13));
  }

  select(event: Event): void { this.file = (event.target as HTMLInputElement).files?.[0] || null; }

  analyze(): void {
    if (this.form.invalid || !this.file) { this.message.set(this.translate.instant('farmer.newIssue.completeFields')); return; }
    this.loading.set(true); this.message.set('');
    this.ai.analyze({ ...this.form.getRawValue() as any, image:this.file }).subscribe({
      next: response => { this.result.set(response.data); this.loading.set(false); },
      error: error => { this.message.set(error.error?.message || this.translate.instant('farmer.newIssue.aiFailed')); this.loading.set(false); }
    });
  }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); this.message.set(this.translate.instant('farmer.newIssue.completeFields')); return; }
    const value = this.form.getRawValue();
    this.submitting.set(true); this.message.set(''); this.success.set(false);
    const payload = {
      title:`${value.domain} issue - ${value.subjectType}`,
      description:String(value.description || ''),
      cropType:value.domain === 'PLANT' ? value.subjectType : null,
      issueCategory:value.issueCategory || null,
      domain:value.domain,
      subjectType:String(value.subjectType || ''),
      symptomsImageUrl:this.result()?.imageUrl || null,
      aiReport:this.result() ? JSON.stringify(this.result()) : null,
      location:{ latitude:Number(value.latitude), longitude:Number(value.longitude) }
    };
    this.bookings.create(payload).subscribe({
      next: response => { this.success.set(true); this.message.set(this.translate.instant('farmer.newIssue.bookingSuccess',{id:response.data.id})); this.submitting.set(false); },
      error: error => { const details=error.error?.data?.fieldErrors ? JSON.stringify(error.error.data.fieldErrors) : ''; this.message.set(error.error?.message || details || this.translate.instant('farmer.newIssue.bookingFailed')); this.submitting.set(false); }
    });
  }
}
