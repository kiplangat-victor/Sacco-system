import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CashTransactionExecutionComponent } from './cash-transaction-execution.component';

describe('CashTransactionExecutionComponent', () => {
  let component: CashTransactionExecutionComponent;
  let fixture: ComponentFixture<CashTransactionExecutionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CashTransactionExecutionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CashTransactionExecutionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
