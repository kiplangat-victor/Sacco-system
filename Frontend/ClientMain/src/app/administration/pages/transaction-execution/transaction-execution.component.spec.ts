import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionExecutionComponent } from './transaction-execution.component';

describe('TransactionExecutionComponent', () => {
  let component: TransactionExecutionComponent;
  let fixture: ComponentFixture<TransactionExecutionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransactionExecutionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionExecutionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
