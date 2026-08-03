import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FarmerSidebar } from './farmer-sidebar';

describe('FarmerSidebar', () => {
  let component: FarmerSidebar;
  let fixture: ComponentFixture<FarmerSidebar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FarmerSidebar],
    }).compileComponents();

    fixture = TestBed.createComponent(FarmerSidebar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
