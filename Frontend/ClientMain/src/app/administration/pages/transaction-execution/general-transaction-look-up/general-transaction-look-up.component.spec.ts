import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralTransactionLookUpComponent } from './general-transaction-look-up.component';

describe('GeneralTransactionLookUpComponent', () => {
  let component: GeneralTransactionLookUpComponent;
  let fixture: ComponentFixture<GeneralTransactionLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GeneralTransactionLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GeneralTransactionLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
