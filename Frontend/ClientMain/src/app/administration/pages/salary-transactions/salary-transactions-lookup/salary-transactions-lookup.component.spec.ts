import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalaryTransactionsLookupComponent } from './salary-transactions-lookup.component';

describe('SalaryTransactionsLookupComponent', () => {
  let component: SalaryTransactionsLookupComponent;
  let fixture: ComponentFixture<SalaryTransactionsLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SalaryTransactionsLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SalaryTransactionsLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
