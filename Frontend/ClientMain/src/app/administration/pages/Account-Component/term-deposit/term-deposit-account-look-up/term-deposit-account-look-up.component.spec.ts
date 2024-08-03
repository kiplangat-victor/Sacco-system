import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TermDepositAccountLookUpComponent } from './term-deposit-account-look-up.component';

describe('TermDepositAccountLookUpComponent', () => {
  let component: TermDepositAccountLookUpComponent;
  let fixture: ComponentFixture<TermDepositAccountLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TermDepositAccountLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TermDepositAccountLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
