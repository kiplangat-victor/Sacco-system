import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoansAccountsLookUpComponent } from './loans-accounts-look-up.component';

describe('LoansAccountsLookUpComponent', () => {
  let component: LoansAccountsLookUpComponent;
  let fixture: ComponentFixture<LoansAccountsLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoansAccountsLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoansAccountsLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
