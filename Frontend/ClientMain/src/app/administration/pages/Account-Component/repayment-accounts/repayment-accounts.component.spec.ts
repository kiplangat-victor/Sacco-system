import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RepaymentAccountsComponent } from './repayment-accounts.component';

describe('RepaymentAccountsComponent', () => {
  let component: RepaymentAccountsComponent;
  let fixture: ComponentFixture<RepaymentAccountsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RepaymentAccountsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RepaymentAccountsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
