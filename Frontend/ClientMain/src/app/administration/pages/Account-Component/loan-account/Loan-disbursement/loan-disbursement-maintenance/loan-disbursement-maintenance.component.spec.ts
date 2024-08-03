import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanDisbursementMaintenanceComponent } from './loan-disbursement-maintenance.component';

describe('LoanDisbursementMaintenanceComponent', () => {
  let component: LoanDisbursementMaintenanceComponent;
  let fixture: ComponentFixture<LoanDisbursementMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoanDisbursementMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoanDisbursementMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
