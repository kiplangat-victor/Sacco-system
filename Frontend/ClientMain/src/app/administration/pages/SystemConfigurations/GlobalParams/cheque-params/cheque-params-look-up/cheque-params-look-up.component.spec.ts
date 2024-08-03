import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChequeParamsLookUpComponent } from './cheque-params-look-up.component';

describe('ChequeParamsLookUpComponent', () => {
  let component: ChequeParamsLookUpComponent;
  let fixture: ComponentFixture<ChequeParamsLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChequeParamsLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChequeParamsLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
