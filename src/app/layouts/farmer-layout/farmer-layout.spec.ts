import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FarmerLayout } from './farmer-layout';

describe('FarmerLayout', () => {
  let component: FarmerLayout;
  let fixture: ComponentFixture<FarmerLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FarmerLayout],
    }).compileComponents();

    fixture = TestBed.createComponent(FarmerLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
