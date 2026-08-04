import { TestBed } from '@angular/core/testing';

import { FarmerProfile } from './farmer-profile';

describe('FarmerProfile', () => {
  let service: FarmerProfile;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FarmerProfile);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
