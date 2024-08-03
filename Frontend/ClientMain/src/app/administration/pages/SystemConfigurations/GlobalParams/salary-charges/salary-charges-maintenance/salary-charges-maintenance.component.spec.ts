import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalaryChargesMaintenanceComponent } from './salary-charges-maintenance.component';

describe('SalaryChargesMaintenanceComponent', () => {
  let component: SalaryChargesMaintenanceComponent;
  let fixture: ComponentFixture<SalaryChargesMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SalaryChargesMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SalaryChargesMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
