import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanAccountMaintenanceComponent } from './loan-account-maintenance.component';

describe('LoanAccountMaintenanceComponent', () => {
  let component: LoanAccountMaintenanceComponent;
  let fixture: ComponentFixture<LoanAccountMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoanAccountMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoanAccountMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
