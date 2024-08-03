import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllAccountsLookUpComponent } from './all-accounts-look-up.component';

describe('AllAccountsLookUpComponent', () => {
  let component: AllAccountsLookUpComponent;
  let fixture: ComponentFixture<AllAccountsLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AllAccountsLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllAccountsLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
