import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalaryTransactionsDataComponent } from './salary-transactions-data.component';

describe('SalaryTransactionsDataComponent', () => {
  let component: SalaryTransactionsDataComponent;
  let fixture: ComponentFixture<SalaryTransactionsDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SalaryTransactionsDataComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SalaryTransactionsDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
