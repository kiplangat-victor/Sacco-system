import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalaryChargesLookUpComponent } from './salary-charges-look-up.component';

describe('SalaryChargesLookUpComponent', () => {
  let component: SalaryChargesLookUpComponent;
  let fixture: ComponentFixture<SalaryChargesLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SalaryChargesLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SalaryChargesLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
