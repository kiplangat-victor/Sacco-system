import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanDisbursementLookUpComponent } from './loan-disbursement-look-up.component';

describe('LoanDisbursementLookUpComponent', () => {
  let component: LoanDisbursementLookUpComponent;
  let fixture: ComponentFixture<LoanDisbursementLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoanDisbursementLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoanDisbursementLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
