import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalaryChargesComponent } from './salary-charges.component';

describe('SalaryChargesComponent', () => {
  let component: SalaryChargesComponent;
  let fixture: ComponentFixture<SalaryChargesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SalaryChargesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SalaryChargesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
