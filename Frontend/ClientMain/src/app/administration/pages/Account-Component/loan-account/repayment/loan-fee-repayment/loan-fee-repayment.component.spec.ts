import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanFeeRepaymentComponent } from './loan-fee-repayment.component';

describe('LoanFeeRepaymentComponent', () => {
  let component: LoanFeeRepaymentComponent;
  let fixture: ComponentFixture<LoanFeeRepaymentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoanFeeRepaymentComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoanFeeRepaymentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
