import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FarmerNavbar } from './farmer-navbar';

describe('FarmerNavbar', () => {
  let component: FarmerNavbar;
  let fixture: ComponentFixture<FarmerNavbar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FarmerNavbar],
    }).compileComponents();

    fixture = TestBed.createComponent(FarmerNavbar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
