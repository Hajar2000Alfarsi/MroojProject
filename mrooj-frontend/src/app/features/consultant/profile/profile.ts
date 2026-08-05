import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import * as L from 'leaflet';
import { ConsultantProfileService } from '../../../core/services/consultant-profile.service';

@Component({
  selector: 'app-consultant-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TranslatePipe],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class ConsultantProfileComponent implements AfterViewInit, OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(ConsultantProfileService);
  private readonly translate = inject(TranslateService);

  @ViewChild('consultantMap') consultantMap?: ElementRef<HTMLDivElement>;

  private map?: L.Map;
  private marker?: L.Marker;
  private viewReady = false;

  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly locating = signal(false);
  readonly locationMessage = signal('');
  readonly message = signal('');
  readonly error = signal('');

  readonly form = this.fb.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    phone: [''],
    specialtyDomain: ['PLANT' as 'PLANT' | 'LIVESTOCK', Validators.required],
    specialtyTags: [''],
    experienceYears: [0, [Validators.required, Validators.min(0)]],
    latitude: [23.588, Validators.required],
    longitude: [58.382, Validators.required],
    available: [true]
  });

  ngOnInit(): void {
    this.service.getMyProfile().subscribe({
      next: response => {
        this.form.patchValue(response.data);
        this.loading.set(false);
        setTimeout(() => {
          this.createOrRefreshMap();
          this.locateCurrentPosition(false);
        }, 0);
      },
      error: error => {
        this.error.set(error.error?.message || this.translate.instant('consultant.profile.loadFailed'));
        this.loading.set(false);
      }
    });
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
    setTimeout(() => this.createOrRefreshMap(), 0);
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  useCurrentLocation(): void {
    this.locateCurrentPosition(true);
  }

  private locateCurrentPosition(showFeedback: boolean): void {
    if (!navigator.geolocation) {
      if (showFeedback) this.locationMessage.set(this.translate.instant('common.locationUnavailable'));
      return;
    }

    this.locating.set(true);
    this.locationMessage.set('');
    navigator.geolocation.getCurrentPosition(
      position => {
        const point = L.latLng(position.coords.latitude, position.coords.longitude);
        this.updateLocation(point, true);
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

  private createOrRefreshMap(): void {
    if (!this.viewReady || this.loading() || !this.consultantMap) return;

    const lat = Number(this.form.value.latitude) || 23.588;
    const lng = Number(this.form.value.longitude) || 58.382;

    if (this.map) {
      this.updateLocation(L.latLng(lat, lng), true);
      this.map.invalidateSize();
      return;
    }

    this.map = L.map(this.consultantMap.nativeElement, {
      center: [lat, lng],
      zoom: 13,
      scrollWheelZoom: true
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    this.marker = L.marker([lat, lng], {
      icon: this.mapIcon(),
      draggable: true,
      title: this.translate.instant('consultant.profile.location')
    }).addTo(this.map);

    this.map.on('click', event => this.updateLocation(event.latlng, false));
    this.marker.on('dragend', () => this.updateLocation(this.marker!.getLatLng(), false));
    setTimeout(() => this.map?.invalidateSize(), 150);
  }

  private mapIcon(): L.DivIcon {
    return L.divIcon({
      className: '',
      html: '<div style="width:26px;height:26px;background:#1e4620;border:4px solid #fff;border-radius:50% 50% 50% 0;transform:rotate(-45deg);box-shadow:0 3px 10px rgba(0,0,0,.38)"></div>',
      iconSize: [26, 26],
      iconAnchor: [13, 26]
    });
  }

  private updateLocation(point: L.LatLng, centerMap: boolean): void {
    this.form.patchValue({ latitude: point.lat, longitude: point.lng });
    this.marker?.setLatLng(point);
    if (centerMap && this.map) this.map.setView(point, Math.max(this.map.getZoom(), 13));
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.message.set('');
    this.error.set('');

    this.service.updateMyProfile(this.form.getRawValue()).subscribe({
      next: response => {
        const stored = JSON.parse(localStorage.getItem('user') || '{}');
        localStorage.setItem('user', JSON.stringify({
          ...stored,
          firstName: response.data.firstName,
          lastName: response.data.lastName,
          phone: response.data.phone
        }));
        this.message.set('consultant.profile.saved');
        this.saving.set(false);
      },
      error: error => {
        this.error.set(error.error?.message || this.translate.instant('consultant.profile.saveFailed'));
        this.saving.set(false);
      }
    });
  }
}
