import { TestBed } from '@angular/core/testing';

import { FarmerDashboard } from './farmer-dashboard';

describe('FarmerDashboard', () => {
  let service: FarmerDashboard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FarmerDashboard);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
