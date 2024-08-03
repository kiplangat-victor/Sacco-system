import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanRepaymentMaintenanceComponent } from './loan-repayment-maintenance.component';

describe('LoanRepaymentMaintenanceComponent', () => {
  let component: LoanRepaymentMaintenanceComponent;
  let fixture: ComponentFixture<LoanRepaymentMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoanRepaymentMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoanRepaymentMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
